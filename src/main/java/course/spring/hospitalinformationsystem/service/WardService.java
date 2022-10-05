package course.spring.hospitalinformationsystem.service;

import course.spring.hospitalinformationsystem.entity.Ward;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;

import java.util.Collection;

public interface WardService {
    Collection<Ward> getAllWards();
    Ward getWardById(Long id) throws NonExistingEntityException;
    Ward getWardByType(String type) throws NonExistingEntityException;
    Ward addWard(Ward ward) throws InvalidEntityDataException;
    Ward updateWard(Ward ward) throws NonExistingEntityException, InvalidEntityDataException;
    Ward deleteWardById(Long id) throws NonExistingEntityException;
    long count();
}
