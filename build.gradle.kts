import com.soywiz.korge.gradle.*

buildscript {
	val korgeVersion: String by project

	repositories {
		mavenLocal()
		maven { url = uri("https://dl.bintray.com/korlibs/korlibs") }
		maven { url = uri("https://plugins.gradle.org/m2/") }
		mavenCentral()
	}
	dependencies {
		classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:1.13.2.3")
	}
}

apply<KorgeGradlePlugin>()

korge {
	id = "not yet"
	version = "0.0.1"
	exeBaseName = "app"
	name = "tetris"
	description = "Tetris is also called Russian Blocks"
	orientation = Orientation.DEFAULT
	copyright = "Copyright (c) 2020"

	// Configuring the author
	authorName = "Gummy Bear"
	authorEmail = "unknown@unknown"
	authorHref = "http://localhost"
	author("name", "email", "href")

	icon = File(rootDir, "icon.png")

	gameCategory = GameCategory.ACTION
	fullscreen = true
	backgroundColor = 0xff000000.toInt()
	appleDevelopmentTeamId = java.lang.System.getenv("DEVELOPMENT_TEAM") ?: project.findProperty("appleDevelopmentTeamId")?.toString()
	appleOrganizationName = "User Name Name"
	entryPoint = "main"
	jvmMainClassName = "MainKt"
	androidMinSdk = null

	cordovaPlugin("name", mapOf("arg1" to "value1"), version = "version")

	//androidAppendBuildGradle("...code...")
	config("MYPROP", "MYVALUE")

	// Korge Plugins
//	plugin("com.soywiz:korge-admob:$korgeVersion", mapOf("ADMOB_APP_ID" to ADMOB_APP_ID))
//	admob(ADMOB_APP_ID) // Shortcut for admob

	cordovaUseCrosswalk()
}