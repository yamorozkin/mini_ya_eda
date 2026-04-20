plugins {
	java
	id("org.springframework.boot") version "4.0.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "delivery"
version = "0.0.1-SNAPSHOT"

allprojects {
	repositories {
		mavenCentral()
	}
}

subprojects {

	val excludedModules = listOf("common-libs")

	if (!excludedModules.contains(project.name)) {

		apply(plugin = "java")
		apply(plugin = "org.springframework.boot")
		apply(plugin = "io.spring.dependency-management")

		java {
			toolchain {
				languageVersion.set(JavaLanguageVersion.of(21))
			}
		}

		dependencies {
			implementation("org.springframework.boot:spring-boot-starter-data-jpa")
			implementation("org.springframework.boot:spring-boot-starter-web")

			//Lombok + Mapstruct
			compileOnly("org.projectlombok:lombok")
			annotationProcessor("org.projectlombok:lombok")
			implementation("org.mapstruct:mapstruct:1.6.3")
			annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
			annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

			//общие либы для API и БД
			implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
			runtimeOnly("org.postgresql:postgresql")

			//библиотека DTO
			implementation(project(":common-libs"))

			//тесты
			testImplementation("org.springframework.boot:spring-boot-starter-test")
		}

		tasks.withType<Test> {
			useJUnitPlatform()
		}
	}
}