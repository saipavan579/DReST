<%@ taglib prefix="s" uri="/struts-tags"%> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="/DReST/struts/dojo/struts_dojo.js"></script>
<script type="text/javascript" src="/DReST/struts/ajax/dojoRequire.js"></script>
<script type="text/javascript" src="/DReST/struts/utils.js" ></script>
<script type="text/javascript" src="/DReST/struts/xhtml/validation.js"></script>
<script type="text/javascript" src="/DReST/struts/css_xhtml/validation.js"></script>
        
<link rel="stylesheet" type="text/css" href="styles.css" />
<div class="content" align="center">
   <div class="leftPart">
        
      <s:if test="hasActionErrors()" >								<!--  Print error messages if any -->
         <div class="errorMessages" >
            <s:actionerror/>
         </div>
      </s:if>
      
      <s:form action="upload" method="post" enctype="multipart/form-data">
         <span class="form_heading">New Patients </span>
         <s:textfield	name="givenName" 		label="Name" 			required="true"/>
         <s:textfield	name="surname"			label="Surname" 		required="true"/>
         <s:textfield	name="age"				label="Age"				required="true"			type="number"/>
         <s:select 		name="gender" 			label="Gender"      	required="true"			list="#{'M':'Male','F':'Female'}"/>
         <s:textfield 	name="city" 			label="City" 			required="true"/>
         <s:textfield 	name="district"			label="District" 		required="true"/>
         <s:textfield 	name="state" 			label="State" 			required="true"/>
         <s:textfield 	name="country" 			label="country" 		required="true"/>
       <%--   <sx:autocompleter name="country" 		label="Country"			list="countries"		onchange="getStates(this.value)"/>
        --%>  
 		 <s:textfield 	name="postalCode" 		label="Postal Code" 	required="true"/>
         <s:file  		name="userImage"		label="Retinal Image"	required="true"/>
         
         <s:if test="{#session.loggedIn==false}">	<!-- While a guest is uploading  -->
            <tr>
               <td class="tdLabel">
                  <label for="agree11" class="label">
                  <a target="_blank" href="<s:url action='showTerms'/>" class="link">Accept Terms</a>
                  </label>
               </td>
               <td>
                  <s:checkbox id="agree11" name="checkMe" theme="simple" />
               </td>
            </tr>
         </s:if>
         <s:elseif test="session.accepted=='false'">	<!-- if the logged in user had not accepted to save images during registration  -->
            <tr>
               <td class="tdLabel">
                  <label for="agree12" class="label">
                  <a target="_blank" href="<s:url action='showTerms'/>" class="link">Accept Terms</a>
                  </label>
               </td>
               <td>
                  <s:checkbox id="agree12" name="checkMe" theme="simple" />
               </td>
            </tr>
         </s:elseif>
         <s:submit	value="upload"/>
      </s:form>
   </div>
   
   <div class="verticalSeperator"></div>
   
   <div class="rightPart">
      <s:form action="upload2" method="post" enctype="multipart/form-data">
         <span class="form_heading">Existing Patients </span>
         <s:textfield	name="uniqueID2" 		label="Enter PatientID" required="true"/>
         <s:file  		name="userImage2"		label="Retinal Image" 	required="true"/>
         
         <s:if test="{#session.loggedIn==false}">	<!-- While a guest is uploading  -->
            <tr>
               <td class="tdLabel">
                  <label for="agree21" class="label">
                  <a target="_blank" href="<s:url action='showTerms'/>" class="link">Accept Terms</a>
                  </label>
               </td>
               <td>
                  <s:checkbox id="agree21" name="checkMe2" theme="simple" />
               </td>
            </tr>
         </s:if>
         <s:elseif test="{accepted=='false'}">	<!-- if the logged in user had not accepted to save images during registration  -->
            <tr>
               <td class="tdLabel">
                  <label for="agree22" class="label">
                  <a target="_blank" href="<s:url action='showTerms'/>" class="link">Accept Terms</a>
                  </label>
               </td>
               <td>
                  <s:checkbox id="agree22" name="checkMe2" theme="simple" />
               </td>
            </tr>
         </s:elseif>
         <s:submit	value="upload"/>
      </s:form>
   </div>

</div>
