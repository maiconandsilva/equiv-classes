package io.github.maiconandsilva.equivclasses.data.services;

import io.github.maiconandsilva.equivclasses.data.entities.AcademicClass;
import io.github.maiconandsilva.equivclasses.data.entities.Course;
import io.github.maiconandsilva.equivclasses.data.entities.EquivalentClass;
import io.github.maiconandsilva.equivclasses.data.repositories.AcademicClassRepository;
import io.github.maiconandsilva.equivclasses.data.repositories.CourseRepository;
import io.github.maiconandsilva.equivclasses.data.repositories.EquivalentClassRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Transactional
@Service("courseManagementService")
public class AcademicClassManagementServiceImpl implements AcademicClassManagementService {
    private final AcademicClassRepository academicClassRepository;
    private final CourseRepository courseRepository;
    private final EquivalentClassRepository equivalentClassRepository;

    public AcademicClassManagementServiceImpl(AcademicClassRepository academicClassRepository,
                                              CourseRepository courseRepository,
                                              EquivalentClassRepository equivalentClassRepository) {
        this.academicClassRepository = academicClassRepository;
        this.courseRepository = courseRepository;
        this.equivalentClassRepository = equivalentClassRepository;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AcademicClass registerClass(Long courseId, AcademicClass academicClass) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        academicClass.setCourse(course);
        return academicClassRepository.save(academicClass);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public EquivalentClass registerClassesEquivalency(Long equivalentClassId, Long ...classId) {
        EquivalentClass equivalentClass;

        if (equivalentClassId == null) {
            equivalentClass = new EquivalentClass();
        } else {
            equivalentClass = equivalentClassRepository.findById(equivalentClassId).orElseThrow();
        }

        for (AcademicClass ac: academicClassRepository.findAllById(Arrays.asList(classId))) {
            equivalentClass.registerEquivalentClass(ac);
        }
        return equivalentClassRepository.save(equivalentClass);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeClassesEquivalency(Long ...classId) {
        Iterable<AcademicClass> academicClasses =
                academicClassRepository.findAllById(Arrays.asList(classId));
        for (AcademicClass ac: academicClasses) {
            ac.getEquivalentClass().removeEquivalentClass(ac);
        }
        academicClassRepository.saveAll(academicClasses);
    }
}
