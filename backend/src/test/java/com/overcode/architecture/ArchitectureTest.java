package com.overcode.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class ArchitectureTest {

  private static final JavaClasses CLASSES = new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages("com.overcode");


  public static final DescribedPredicate<JavaClass> GLOBAL_EXCEPTION_HANDLER = new DescribedPredicate<>("GlobalExceptionHandler") {
    @Override
    public boolean test(JavaClass javaClass) {
      return javaClass.getFullName().equals(
              "com.overcode.controller.exception.GlobalExceptionHandler");
    }
  };

  @Test
  void controllerDebeTenerDTOControllersYExcepciones() {
    classes()
        .that().resideInAPackage("..controller..").and().haveSimpleNameNotEndingWith("Test")
        .should().haveSimpleNameEndingWith("Controller")
            .orShould().haveSimpleNameEndingWith("DTO")
            .orShould().haveSimpleNameContaining("Exception")
        .check(CLASSES);
  }

  @Test
  void servicesDebenTenerDTOServicesYExcepciones() {
    classes()
        .that().resideInAPackage("..service..").and().haveSimpleNameNotEndingWith("Test")
        .should().haveSimpleNameEndingWith("Service")
        .orShould().haveSimpleNameEndingWith("ServiceImpl")
            .orShould().haveSimpleNameEndingWith("DTO")
            .orShould().haveSimpleNameEndingWith("Exception")
        .check(CLASSES);
  }

  @Test
  void repositoryTieneDAOsYRepositories() {
    classes()
        .that().resideInAPackage("..repository..").and()
            .resideOutsideOfPackage("..scraper..")
        .should().haveSimpleNameEndingWith("Repository")
            .orShould().haveSimpleNameContaining("DAO")
            .orShould().haveSimpleNameEndingWith("RepositoryImpl")
        .check(CLASSES);
  }

  @Test
  void persistenciaTieneDTOsYExcepciones() {
    classes()
            .that().resideInAPackage("..persistence..").and()
            .resideOutsideOfPackage("..mapper..").and()
            .resideOutsideOfPackage("..repository..")
            .should().haveSimpleNameEndingWith("DTO")
            .orShould().haveSimpleNameEndingWith("Exception")
            .check(CLASSES);
  }


  @Test
  void controllersDebenTenerAnnotation() {
    classes()
        .that().resideInAPackage("..controller..").and().haveSimpleNameNotEndingWith("Test")
        .and().haveSimpleNameEndingWith("Controller")
        .should().beAnnotatedWith(RestController.class)
        .orShould().beAnnotatedWith(Controller.class)
        .check(CLASSES);
  }

  @Test
  void servicesDebenTenerAnnotation() {
    classes()
        .that().resideInAPackage("..service..")
        .and().haveSimpleNameEndingWith("ServiceImpl")
        .should().beAnnotatedWith(Service.class)
        .check(CLASSES);
  }

  @Test
  void repositoryTieneInterface() {
    classes()
        .that().resideInAPackage("..repository..")
        .and().haveSimpleNameEndingWith("Repository")
        .should().beInterfaces()
        .check(CLASSES);
  }

  @Test
  void controllerNoTieneRepository() {
    noClasses()
        .that().resideInAPackage("..controller..")
        .should().accessClassesThat().resideInAPackage("..repository..")
        .because("Controllers should use services, not repositories directly")
        .check(CLASSES);
  }

  @Test
  void controllersShouldNotReturnEntities() {
    noMethods()
        .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
        .should().haveRawReturnType(annotatedWith(Entity.class))
        .because("Controllers should return DTOs, not JPA entities")
        .check(CLASSES);
  }

  @Test
  void entitiesShouldNotBeInApiPackages() {
    classes()
        .that().areAnnotatedWith(Entity.class)
        .should().resideInAnyPackage("..persistence..")
        .because("JPA entities should stay in the persistence layer")
        .check(CLASSES);
  }

  @Test
  void entitiesShouldUseJavaTime() {
    noClasses()
        .that().areAnnotatedWith(Entity.class)
        .should().accessClassesThat().belongToAnyOf(java.util.Date.class, java.util.Calendar.class)
        .because("Use java.time classes (LocalDate, LocalDateTime) instead of legacy date classes")
        .check(CLASSES);
  }

  @Test
  void noGenericExceptions() {
    noClasses()
        .should().callConstructor(Exception.class)
        .because("Use specific exception types")
        .check(CLASSES);
    noMethods()
        .should().declareThrowableOfType(Exception.class)
        .because("Use specific exception types")
        .check(CLASSES);
  }

  @Test
  void noFieldInjection() {
    noFields()
        .should().beAnnotatedWith(Autowired.class)
        .because("Use constructor injection instead of field injection")
        .check(CLASSES);
  }

  @Test
  void noAnnotationsDePersistenciaEnModelo() {
    noClasses()
            .that().resideInAPackage("..model..")
            .should().beAnnotatedWith("org.springframework..")
            .orShould().beAnnotatedWith("jakarta.persistence..")
            .orShould().beAnnotatedWith("javax.persistence..")
            .orShould().beAnnotatedWith("org.hibernate..")
            .because("Model layer should be framework-independent - no Spring, JPA, or Hibernate annotations")
            .check(CLASSES);
  }

  @Test
  void noRecursionEntrePaquetes() {
          slices().matching("com.example.myapp.(*)..")
                  .should().beFreeOfCycles().because("Escalabilidad y testing separado");
  }

  @Test
  void respetaArquitecturaEnCapas() {
          layeredArchitecture()
                  .consideringAllDependencies()
                  .ignoreDependency(
                          GLOBAL_EXCEPTION_HANDLER,
                          DescribedPredicate.<JavaClass>alwaysTrue())
                  .layer("Controller").definedBy("com.overcode.controller..")
                  .layer("Service").definedBy("com.overcode.service..")
                  .layer("Persistence").definedBy("com.overcode.persistence..")
                  .layer("Repository").definedBy("..repository..")
                  .layer("Model").definedBy("com.overcode.model..")
                  .layer("Config").definedBy("com.overcode.config..")
                  .layer("Application").definedBy("com.overcode")
                  .layer("Security").definedBy("com.overcode.security..")

                  .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                  .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service", "Config")
                  .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service", "Repository", "Persistence")
                  .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service", "Repository")
                  .whereLayer("Model").mayOnlyBeAccessedByLayers("Controller", "Service", "Repository", "Model", "Config", "Security", "Persistence")
                  .whereLayer("Security").mayOnlyBeAccessedByLayers("Security", "Config", "Service")
                  .whereLayer("Config").mayOnlyBeAccessedByLayers("Config", "Application")

                  .because("Hay que mantener arquitectura en capas para mejor organización")
                  .check(CLASSES);
}
}