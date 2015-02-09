package com.Daos;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Map;

import com.Beans.UploadBean;
import com.opensymphony.xwork2.ActionContext;


public class UploadDao {

	public String upload(UploadBean uploadBean, String imageID, String filePath) throws Exception {
		String SUCCESS = "success";
		String ERROR = "error";

		Connection DBConnection = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		FileInputStream fis = null;
		String username = "Guest";

		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		if (session.get("loggedIn") == "true") username = (String) session.get("user");

		java.util.Date date = new Date(System.currentTimeMillis());
		Timestamp timestamp = new Timestamp(date.getTime());

		try {

			fis = new FileInputStream(uploadBean.getUserImage());
			DBConnection = ConnMgr.getConnection();
			ps = DBConnection.prepareStatement("insert into patientDetails values(?,?,?,?,?,?,?,?,?,?)");

			ps.setString(1, uploadBean.getPatientID());
			ps.setString(2, uploadBean.getGivenName());
			ps.setString(3, uploadBean.getSurname());
			ps.setInt(4, uploadBean.getAge());
			//System.out.println(String.valueOf(uploadBean.getGender()));
			ps.setString(5, String.valueOf(uploadBean.getGender()));
			ps.setString(6, uploadBean.getCity());
			ps.setString(7, uploadBean.getDistrict());
			ps.setString(8, uploadBean.getState());
			ps.setString(9, uploadBean.getCountry());
			ps.setString(10, uploadBean.getPostalCode());

			int count = ps.executeUpdate();
			InputStream inputStream = new FileInputStream(new File(filePath + "/" + imageID + "_exu_OD.jpg"));

			ps1 = DBConnection.prepareStatement("insert into images values(?,?,?,?,?,?,?,?,?)");
			ps1.setBinaryStream(1, fis, (int)(uploadBean.getUserImage().length()));
			ps1.setString(2, imageID);
			ps1.setString(3, uploadBean.getPatientID());
			ps1.setString(4, username);
			ps1.setTimestamp(5, timestamp);
			ps1.setBoolean(6, true);
			ps1.setBlob(7, inputStream);
			ps1.setString(8, "Diabetic Retinopathy");
			ps1.setString(9, "Diabetic Retinopathy");

			count += ps1.executeUpdate();
			if (count == 2) {
				/* System.out.println("The image has been inserted successfully");*/
				return SUCCESS;
			} else {
				try {
					PreparedStatement ps2 = DBConnection.prepareStatement("delete from images where patientID=?");
					ps2.executeUpdate();
					ps2 = DBConnection.prepareStatement("delete from patientDetails where patientID=?");
					ps2.executeUpdate();
					ps2.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				/*System.out.println("The image did not insert successfully");*/
				return ERROR;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
					ps = null;
				}
				if (DBConnection != null) {
					DBConnection.close();
					DBConnection = null;
				}
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ERROR;
	}
}
