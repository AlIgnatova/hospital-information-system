package course.spring.hospitalinformationsystem.service;

import course.spring.hospitalinformationsystem.entity.HospitalStay;
import course.spring.hospitalinformationsystem.entity.Test;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;

import java.util.Collection;
import java.util.List;

public interface HospitalStayService {

    Collection<HospitalStay> getAllHospitalStays();
    HospitalStay getHospitalStayById(Long id) throws NonExistingEntityException;
    Collection<Test> getAllTestsForHospitalStayById(Long id) throws NonExistingEntityException;
    HospitalStay addHospitalStay(HospitalStay hospitalStay) throws InvalidEntityDataException;
    HospitalStay updateHospitalStay(HospitalStay hospitalStay) throws NonExistingEntityException, InvalidEntityDataException;
    HospitalStay addDecursus(Long id, String decursus);

    HospitalStay editDecursus(Long id, Long decId, String decursus);
    HospitalStay deleteDecursus(Long id, Long decId);
    HospitalStay addTreatment(Long id, List<String> medications);
    HospitalStay dischargePatient(Long id, HospitalStay stay) throws NonExistingEntityException;
    String getDischargeSummary (Long id) throws NonExistingEntityException;
    HospitalStay deleteHospitalStayById(Long id) throws NonExistingEntityException;
    long count();
}
