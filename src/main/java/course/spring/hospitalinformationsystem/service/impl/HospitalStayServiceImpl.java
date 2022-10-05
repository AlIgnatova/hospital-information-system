package course.spring.hospitalinformationsystem.service.impl;

import course.spring.hospitalinformationsystem.dao.HospitalStayRepository;
import course.spring.hospitalinformationsystem.dao.PatientRepository;
import course.spring.hospitalinformationsystem.dao.WardRepository;
import course.spring.hospitalinformationsystem.entity.*;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;
import course.spring.hospitalinformationsystem.service.DecursusService;
import course.spring.hospitalinformationsystem.service.HospitalStayService;
import course.spring.hospitalinformationsystem.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.*;

import static course.spring.hospitalinformationsystem.service.serviceUtils.UserManager.getLoggedUser;

@Service
@Transactional
@Slf4j
public class HospitalStayServiceImpl implements HospitalStayService {

    private DecursusService decursusService;
    private PatientService patientService;

    private HospitalStayRepository hospitalStayRepo;
    private PatientRepository patientRepo;
    private WardRepository wardRepo;

    @Autowired
    public HospitalStayServiceImpl(DecursusService decursusService, PatientService patientService,
                                   HospitalStayRepository hospitalStayRepo, PatientRepository patientRepo,
                                   WardRepository wardRepo) {
        this.decursusService = decursusService;
        this.patientService = patientService;
        this.hospitalStayRepo = hospitalStayRepo;
        this.patientRepo = patientRepo;
        this.wardRepo = wardRepo;
    }

    /**
     * @return collection of all HospitalStays in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<HospitalStay> getAllHospitalStays() {
        return hospitalStayRepo.findAll();
    }

    /**
     * @param id of the HospitalStay that will be returned
     * @return HospitalStay with param id
     * @throws NonExistingEntityException if HospitalStay with param ID is not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public HospitalStay getHospitalStayById(Long id) throws NonExistingEntityException {
        return hospitalStayRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Hospital stay with id='%d' does not exist.", id)));
    }

    /**
     * @param id ID of the HospitalStay, whose Tests will be collected
     * @return Collection of all Tests with HospitalStay id corresponding to the param id
     * @throws NonExistingEntityException if HospitalStay with param ID is not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<Test> getAllTestsForHospitalStayById(Long id) throws NonExistingEntityException {

        return getHospitalStayById(id).getTestsList();
    }

    /**
     * @param hospitalStay that will be added to the repository
     * @return the created HospitalStay entity
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    public HospitalStay addHospitalStay(HospitalStay hospitalStay) throws InvalidEntityDataException {
        if (!patientRepo.findById(hospitalStay.getPatient().getId()).isPresent()) {
            throw new NonExistingEntityException(String.format(
                    "Patient with EGN ='%s' does not exist and needs to be added first!", hospitalStay.getPatient().getEGN()
            ));
        }
        Ward ward = hospitalStay.getWard();
        if (ward.getBedAvailability() < 1) {
            throw new InvalidEntityDataException(String.format
                    ("There are no more available beds in Ward '%s'.", String.valueOf(ward.getWardType())));
        }
        Patient patient = hospitalStay.getPatient();
        if (patient.getWard() != null) {
            throw new InvalidEntityDataException(String.format
                    ("Patient %s %s %s is already hospitalized in Ward: %s.", patient.getFirstName(), patient.getMiddleName(),
                            patient.getLastName(), patient.getWard().getWardType().toString()));
        }
        hospitalStay.setId(null);
        hospitalStay.setAdmissionDateAndTime(LocalDateTime.now());
        hospitalStay.setModified(LocalDateTime.now());
        HospitalStay created = hospitalStayRepo.save(hospitalStay);
        ward.setBedAvailability(ward.getBedAvailability() - 1);
        ward.setBedOccupancy(ward.getBedCapacity() - ward.getBedAvailability());
        List<Patient> patientsList = new ArrayList<>(ward.getPatients());
        patientsList.add(created.getPatient());
        ward.setPatients(patientsList);
        wardRepo.save(ward);

        patient.setWard(ward);

        List<HospitalStay> patientStaysList = new ArrayList<>(patient.getHospitalStaysCollection());
        patientStaysList.add(created);
        patient.setHospitalStaysCollection(patientStaysList);
        patientService.updatePatient(patient);

        return created;
    }

    /**
     * @param hospitalStay that will be updated
     * @return the updated HospitalStay
     * @throws NonExistingEntityException in case the input HospitalStay's ID does not match any existing Test in the repository
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    @RolesAllowed({"ADMINISTRATOR", "DOCTOR"})
    public HospitalStay updateHospitalStay(HospitalStay hospitalStay) throws NonExistingEntityException, InvalidEntityDataException {
        HospitalStay oldStay = getHospitalStayById(hospitalStay.getId());
        if (!hospitalStay.getId().equals(oldStay.getId())) {
            throw new InvalidEntityDataException("Hospital stay id cannot be changed");

        }

        hospitalStay.setAdmissionDateAndTime(oldStay.getAdmissionDateAndTime());
        hospitalStay.setModified(LocalDateTime.now());
        return hospitalStayRepo.save(hospitalStay);
    }

    /**
     * @param id       ID of the HospitalStay where Decursus will be added
     * @param decursus The value of the "text" property of the Decursus that will be added to the
     *                 Decursus collection in HospitalStay
     * @return HospitalStay with updated Decursus collection
     */
    @Override
    public HospitalStay addDecursus(Long id, String decursus) {
        HospitalStay stay = hospitalStayRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Hospital stay with id ='%d' does not exist.", id)
        ));
        if (!stay.isStayActive()) {
            throw new InvalidEntityDataException("Patient is already discharged.");
        }
        Decursus dec = new Decursus();
        dec.setText(decursus);
        dec.setHospitalStay(stay);
        dec.setAddedBy(getLoggedUser());
        List<Decursus> decursusList = new ArrayList<>(stay.getDecursusList());
        decursusList.add(decursusService.addDecursus(dec));
        stay.setDecursusList(new HashSet<>(decursusList));
        return updateHospitalStay(stay);
    }

    /**
     * @param id       ID of the HospitalStay, whose decursus collection will be edited
     * @param decId    ID of the Decursus, that will be modified
     * @param decursus new value for property "text" of the Decursus that will be modified
     * @return HospitalStay with updated Decursus collection
     */
    @Override
    @RolesAllowed({"ADMINISTRATOR", "DOCTOR"})
    public HospitalStay editDecursus(Long id, Long decId, String decursus) {
        HospitalStay stay = hospitalStayRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Hospital stay with id ='%d' does not exist.", id)
        ));
        if (!stay.isStayActive()) {
            throw new InvalidEntityDataException("Patient is already discharged.");
        }
        Decursus oldDec = decursusService.getDecursusById(decId);
        List<Decursus> decList = new ArrayList<>(stay.getDecursusList());
        int index = decList.indexOf(oldDec);
        decList.set(index, decursusService.editDecursus(decId, decursus));
        stay.setDecursusList(new HashSet<>(decList));
        return updateHospitalStay(stay);
    }

    /**
     * @param id    ID of the HospitalStay, whose Decursus collection will be modified
     * @param decId ID of the Decursus that will be deleted
     * @return HospitalStay with updated Decursus colletion
     */
    @Override
    @RolesAllowed({"ADMINISTRATOR", "DOCTOR"})
    public HospitalStay deleteDecursus(Long id, Long decId) {
        HospitalStay stay = hospitalStayRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Hospital stay with id ='%d' does not exist.", id)
        ));
        if (!stay.isStayActive()) {
            throw new InvalidEntityDataException("Patient is already discharged.");
        }
        Decursus oldDec = decursusService.deleteDecursus(decId);
        List<Decursus> decList = new ArrayList<>(stay.getDecursusList());
        int index = decList.indexOf(oldDec);
        decList.remove(index);
        stay.setDecursusList(new HashSet<>(decList));
        return updateHospitalStay(stay);
    }

    /**
     * @param id          ID of the HospitalStay whose treatment collection will be modified
     * @param medications List of Strings with the elements that will be added to the treatment collection
     * @return updated with added treatment HospitalStay
     */
    @Override
    public HospitalStay addTreatment(Long id, List<String> medications) {
        HospitalStay stay = hospitalStayRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Hospital stay with id ='%d' does not exist.", id)
        ));
        if (!stay.isStayActive()) {
            throw new InvalidEntityDataException("Patient is alredy discharged.");
        }
        List<String> currentMeds = new ArrayList<>(stay.getMedicationsList().stream().toList());
        stay.getMedicationsList().addAll(medications);
        return updateHospitalStay(stay);
    }

    /**
     * @param id           ID of the HospitalStay, whose "isActive" property will be set to "false" value
     * @param hospitalStay input HospitalStay entity with changes that will be applied to the HospitalStay
     * @return the updated HospitalStay
     * @throws NonExistingEntityException if HospitalStay with param ID is not present in the repository
     */
    @Override
    public HospitalStay dischargePatient(Long id, HospitalStay hospitalStay) throws NonExistingEntityException {
        if (!hospitalStayRepo.findById(id).isPresent()) {
            throw new NonExistingEntityException(String.format(
                    "Hospital stay with id ='%s' does not exist.", id
            ));
        }
        if (!getHospitalStayById(id).isStayActive()) {
            throw new InvalidEntityDataException("Patient is already discharged.");
        }

        hospitalStay.setStayActive(false);
        hospitalStay.setDischargeDateAndTime(LocalDateTime.now());
        Patient patient = hospitalStay.getPatient();
        patient.setModified(LocalDateTime.now());
        patient.setWard(null);
        patientRepo.save(patient);

        Ward ward = hospitalStay.getWard();
        ward.setBedAvailability(ward.getBedAvailability() + 1);
        ward.setBedOccupancy(ward.getBedCapacity() - ward.getBedAvailability());
        List<Patient> patients = new ArrayList<>(ward.getPatients());
        patients.remove(hospitalStay.getPatient());
        ward.setPatients(new ArrayList<>(patients));
        ward.setModified(LocalDateTime.now());
        wardRepo.save(ward);

        return updateHospitalStay(hospitalStay);
    }

    /**
     * @param id ID of the HospitalStay, whose discharge summary will be provided
     * @return String value of the discharge summary
     * @throws NonExistingEntityException if HospitalStay with param ID is not present in the repository
     */
    @Override
    public String getDischargeSummary(Long id) throws NonExistingEntityException {
        HospitalStay hospitalStayCurrent = getHospitalStayById(id);
        return hospitalStayCurrent.DischargeSummary();
    }

    /**
     * @param id ID of the HospitalStay that will be deleted
     * @return the HospitalStay that was deleted
     * @throws NonExistingEntityException if HospitalStay with param id is not present in the repository
     */
    @Override
    public HospitalStay deleteHospitalStayById(Long id) throws NonExistingEntityException {
        HospitalStay oldStay = getHospitalStayById(id);
        hospitalStayRepo.deleteById(id);
        return oldStay;
    }

    /**
     * @return count of all HospitalStays in the repository
     */
    @Override
    public long count() {
        return hospitalStayRepo.count();
    }

}
