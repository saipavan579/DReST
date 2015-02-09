package com.ActionClasses;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;

import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.util.ServletContextAware;

import com.Beans.UploadBean;
import com.Daos.CheckAcceptance;
import com.Daos.CheckPrimaryKey;
import com.Daos.UploadDao;
import com.mysql.jdbc.StringUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class UploadAction extends ActionSupport implements ServletContextAware, ModelDriven < UploadBean > {

	private static final long serialVersionUID = 4591767228613894989L;
	private ServletContext servletContext;
	
	private UploadBean uploadBean = new UploadBean();
	String username = "Guest";
	static int dailyCounter = 0;

	static MatlabProxyFactory[] factory = {null, null, null, null};
	static MatlabProxy[] proxy 			= {null, null, null, null};
	static int instance = 0;

	public void validate() 
	{
		if (StringUtils.isEmptyOrWhitespaceOnly(uploadBean.getGivenName()) 		|| 
				StringUtils.isEmptyOrWhitespaceOnly(uploadBean.getSurname())	|| 
				StringUtils.isEmptyOrWhitespaceOnly(uploadBean.getCity()) 		|| 
				StringUtils.isEmptyOrWhitespaceOnly(uploadBean.getDistrict()) 	|| 
				StringUtils.isEmptyOrWhitespaceOnly(uploadBean.getState()) 		|| 
				StringUtils.isEmptyOrWhitespaceOnly(uploadBean.getCountry())) 
		{
			addActionError("All fields with * are required");
			return;
		}
		if (uploadBean.getAge() < 1 || uploadBean.getAge() > 100) {
			addActionError("Enter a valid number between 1 to 100 in 'Age'");
			return;
		}
	}

	@SuppressWarnings("unchecked")
	public String execute() {

		Boolean canSave = false;
		String loggedIn = "false";

		String counter;
		int remainingCharsInUniqueID;
		String imageID;
		String patientID;

		/*------------------------------------- Create UniqueID for patient and image ---------------------------------------*/

		DateFormat df = new SimpleDateFormat("ddMMyy");
		Calendar calobj = Calendar.getInstance();
		String Date = df.format(calobj.getTime()); //Get Date as String

		dailyCounter = CheckPrimaryKey.getNextValue(Date);
		dailyCounter++;

		counter = String.valueOf(dailyCounter);
		remainingCharsInUniqueID = 5 - counter.length();
		for (int i = 0; i < remainingCharsInUniqueID; i++) counter = '0' + counter;

		imageID = 'O' + Date + counter;
		patientID = 'P' + Date + counter;

		uploadBean.setUserImageFileName(imageID + ".jpg");
		uploadBean.setPatientID(patientID);

		/*------------------------------------- Working on uploaded image ---------------------------------------*/

		try {
			String filePath = servletContext.getRealPath("/"); 
			File fileToCreate = new File(filePath, uploadBean.getUserImageFileName());
			FileUtils.copyFile(uploadBean.getUserImage(), fileToCreate);

			if (factory[instance] == null) {
				System.out.println("Creating instace " + instance);

				MatlabProxyFactoryOptions.Builder builder = new MatlabProxyFactoryOptions.Builder();

				//builder.setHidden(true);							// MATLAB should appear hidden.
				
				Random rand = new Random();
			    int randomNum = rand.nextInt(1000) + 2000;
				builder.setPort(randomNum);							// Set the port matlabcontrol uses to communicate with MATLAB.
				
				//builder.setUsePreviouslyControlledSession(true);
				
				MatlabProxyFactoryOptions options = builder.setMatlabLocation("/usr/local/MATLAB/R2014a/bin/matlab").build();

				factory[instance] = new MatlabProxyFactory(options);
				proxy[instance] = factory[instance].getProxy();
				proxy[instance].eval("addpath SourceCode/Hipc_New/");
			}

			proxy[instance].feval("enhance", filePath + "/" + uploadBean.getUserImageFileName());
			instance = (instance + 1) % 3;

			/*------------------------------------------------Code for saving image--------------------------------------------------*/

			@SuppressWarnings("rawtypes")
			Map session = ActionContext.getContext().getSession();
			loggedIn = (String) session.get("loggedIn");

			if (loggedIn != null) {
				username = (String) session.get("user");
				canSave = CheckAcceptance.check(username);	//check whether loggedin user accepted terms while registering
			}

			if (canSave || uploadBean.isCheckMe()) {
				session.put("imageID", imageID);
				UploadDao uploadDao = new UploadDao();
				String result = uploadDao.upload(uploadBean, imageID, filePath);
				uploadBean.setUserImageFileName(imageID + "_exu_OD.jpg");
				return result;
			}
			uploadBean.setUserImageFileName(imageID + "_exu_OD.jpg");

		} catch (Exception e) {
			e.printStackTrace();
			addActionError(e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	@Override
	public UploadBean getModel() {
		return uploadBean;
	}

	public void setServletContext(ServletContext context) {
		servletContext = context;
	}

}
