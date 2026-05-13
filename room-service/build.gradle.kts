import com.google.protobuf.gradle.id

plugins {
    id("io.micronaut.application") version "4.4.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.google.protobuf") version "0.9.4"
}

version = "1.0"
group = "com.motel.room"

repositories {
    mavenCentral()
}

dependencies {

    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")

    implementation("io.micronaut.validation:micronaut-validation")

    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")

    implementation("io.micronaut.sql:micronaut-jdbc-hikari")

    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("io.grpc:grpc-netty-shaded:1.68.1")
    implementation("io.grpc:grpc-protobuf:1.68.1")
    implementation("io.grpc:grpc-stub:1.68.1")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")

    runtimeOnly("com.mysql:mysql-connector-j")

    runtimeOnly("ch.qos.logback:logback-classic")

    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
}

application {
    mainClass.set("com.motel.RoomApplication")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

micronaut {
    runtime("netty")
    testRuntime("junit5")

    processing {
        incremental(true)
        annotations("com.motel.*")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.68.1"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}
