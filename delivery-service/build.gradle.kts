plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
}


group = "delivery"
version = "0.0.1-SNAPSHOT"



repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common-libs"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    //lombok
    implementation("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    //additional
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
}

tasks.test {
    useJUnitPlatform()
}