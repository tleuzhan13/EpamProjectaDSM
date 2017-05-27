package com.epam.adsm.action.implementation.post;

import com.epam.adsm.action.Action;
import com.epam.adsm.action.ActionConstants;

import com.epam.adsm.action.ActionResult;
import com.epam.adsm.model.Patient;
import com.epam.adsm.model.Staff;
import com.epam.adsm.service.CordinatorService;
import com.epam.adsm.service.PersonService;
import com.epam.adsm.service.ServiceExeption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.epam.adsm.action.ActionConstants.*;


/**
 * Created by akmatleu on 18.05.17.
 */
public class SignInAction implements Action {

    private static final Logger LOG = LoggerFactory.getLogger(SignInAction.class);

    @Override
    public ActionResult execute(HttpServletRequest request, HttpServletResponse response)  {
        LOG.info("Come to SIGN IN ACTION");

        String phoneNumber = request.getParameter(ActionConstants.PHONE);
        String password = request.getParameter(ActionConstants.PASSWORD);
        Staff staff;
        Patient patient;

        PersonService personService = new PersonService();
        try {
            staff = personService.findStaffByPhoneAndPassword(phoneNumber,password);
            if(staff!=null) {
                HttpSession session = request.getSession();
                session.setAttribute(STAFF_ID, staff.getId());
                session.setAttribute(ROLE, staff.getRole());
                session.setAttribute(NAME, staff.getName());
                LOG.info("Staff id :"+staff.getId()+ " Role:"+staff.getRole());
                return new ActionResult(HOME_PAGE, true);
            }
            patient = personService.findPatientByPhoneAndPassword(phoneNumber,password);
            if(patient!=null){
                HttpSession session = request.getSession();
                Staff doctor = patient.getDoctor();
                CordinatorService cordinatorService =new CordinatorService();
                doctor = cordinatorService.findStaffById(doctor.getId());
                session.setAttribute(PATIENT_CODE,patient.getPatientCode());
                session.setAttribute(ROLE,PATIENT_ROLE);
                session.setAttribute(PATIENT_DOCTOR_SURNAME,doctor.getSurname());
                return new ActionResult(PATIENT_HOME_PAGE,true);
            }  else {
                request.setAttribute(LOGIN_ERROR,true);
                return new ActionResult(LOGIN_PAGE);
            }
        } catch (ServiceExeption e) {
            LOG.error("Cannot sign in",e);
        }
        return new ActionResult(LOGIN_PAGE,true);
    }
}