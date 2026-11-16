package com.overcode.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchitectureTest {

  private static final JavaClasses CLASSES = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages("com.overcode");

  @Test
  public void controllerDebeTenerDTOControllersYExcepciones() {
    classes()
        .that().resideInAPackage("..controller..").and().haveSimpleNameNotEndingWith("Test")
        .should().haveSimpleNameEndingWith("Controller")
            .orShould().haveSimpleNameEndingWith("DTO")
            .orShould().haveSimpleNameContaining("Exception")
        .check(CLASSES);
  }

  @Test
  public void servicesDebenTenerDTOServicesYExcepciones() {
    classes()
        .that().resideInAPackage("..service..").and().haveSimpleNameNotEndingWith("Test")
        .should().haveSimpleNameEndingWith("Service")
        .orShould().haveSimpleNameEndingWith("ServiceImpl")
            .orShould().haveSimpleNameEndingWith("DTO")
            .orShould().haveSimpleNameEndingWith("Exception")
        .check(CLASSES);
  }

  @Test
  public void repositoryTieneDAOsYRepositories() {
    classes()
        .that().resideInAPackage("..repository..").and()
            .resideOutsideOfPackage("..scraper").and()
            .haveSimpleNameNotEndingWith("Test")
        .should().haveSimpleNameEndingWith("Repository")
            .orShould().haveSimpleNameContaining("DAO")
            .orShould().haveSimpleNameEndingWith("RepositoryImpl")
        .check(CLASSES);
  }

  @Test
  public void persistenciaTieneDTOsYExcepciones() {
    classes()
            .that().resideInAPackage("..persistence..").and()
            .resideOutsideOfPackage("..mapper")
            .and().haveSimpleNameNotEndingWith("Test")
            .should().haveSimpleNameEndingWith("DTO")
            .orShould().haveSimpleNameEndingWith("Exception")
            .check(CLASSES);
  }


  @Test
  public void controllersDebenTenerAnnotation() {
    classes()
        .that().resideInAPackage("..controller..").and().haveSimpleNameNotEndingWith("Test")
        .and().haveSimpleNameEndingWith("Controller")
        .should().beAnnotatedWith(RestController.class)
        .orShould().beAnnotatedWith(Controller.class)
        .check(CLASSES);
  }

  @Test
  public void servicesDebenTenerAnnotation() {
    classes()
        .that().resideInAPackage("..service..")
        .and().haveSimpleNameEndingWith("ServiceImpl")
        .should().beAnnotatedWith(Service.class)
        .check(CLASSES);
  }

  @Test
  public void repositoryTieneInterface() {
    classes()
        .that().resideInAPackage("..repository..")
        .and().haveSimpleNameEndingWith("Repository")
        .should().beInterfaces()
        .check(CLASSES);
  }

  @Test
  public void controllerNoTieneRepository() {
    noClasses()
        .that().resideInAPackage("..controller..")
        .should().accessClassesThat().resideInAPackage("..repository..")
        .because("Controllers should use services, not repositories directly")
        .check(CLASSES);
  }

  @Test
  public void noAnnotationsDePersistenciaEnModelo() {
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
  public void respetaArquitecturaEnCapas() {
          layeredArchitecture()
                  .consideringAllDependencies()
                  .ignoreDependency("..controller.exception.GlobalExceptionHandler", "..")
                  .layer("Controller").definedBy("com.overcode.controller..")
                  .layer("Service").definedBy("com.overcode.service..")
                  .layer("Persistence").definedBy("com.overcode.persistence..")
                  .layer("Repository").definedBy("com.overcode.repository..")
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
                  .check(CLASSES);
}
}