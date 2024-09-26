plugins {
	java
    jacoco
	id("org.springframework.boot") version "2.7.7"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("org.sonarqube") version "5.0.0.4638"
}

group = "tinygames"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

val springfoxVersion = "3.0.0"
val lombokVersion = "1.18.30"

dependencies {
	implementation ("io.springfox:springfox-boot-starter:$springfoxVersion")
	implementation ("io.springfox:springfox-swagger2")
	implementation ("io.springfox:springfox-swagger-ui")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("junit:junit")

    compileOnly("org.projectlombok:lombok:$lombokVersion")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

sonarqube {
    properties {
        property("sonar.junit.reportPaths", "build/test-results/test/*")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}