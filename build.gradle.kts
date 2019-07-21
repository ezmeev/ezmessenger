plugins {
	application
}

application {
	mainClassName = "samples.HelloWorld"
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
	compile("com.fasterxml.jackson.core:jackson-core:2.9.9")
	compile("com.fasterxml.jackson.core:jackson-databind:2.9.9")
	testCompile("junit:junit:4.12")
}

repositories {
	jcenter()
}
