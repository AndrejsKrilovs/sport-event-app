plugins {
  id("java")
  id("org.springframework.boot") version "3.5.13"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "krilovs.andrejs.enlabs.interview"
version = "1.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.mockito:mockito-core")
}

tasks.test {
  useJUnitPlatform()
}