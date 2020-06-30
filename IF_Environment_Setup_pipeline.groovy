def ARM_USERNAME_VALUE
def ARM_PASSWORD_VALUE
node {
	checkout scm
	properties = readProperties file: EnvironmentFile
	jenkins = readProperties file: 'jenkins-pipeline/external-team/ENV_G_26JUN20/if-jenkins.properties'
	propertiesUtil = load 'jenkins-pipeline-utils/utility/MRT_Utils.groovy'
	propertiesUtil.setPropertyFile(properties)
	propertiesUtil.setJenkinsPropertyFile(jenkins)
	try{
		withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: properties.get('ARM_CREDENTIAL_ID'),
				usernameVariable: 'ARM_USERNAME', passwordVariable: 'ARM_PASSWORD']]) {
			env.ARM_PASSWORD_VALUE=ARM_PASSWORD
			env.ARM_USERNAME_VALUE=ARM_USERNAME
		}
	}
	catch (err){
		env.ARM_PASSWORD_VALUE=''
		echo "WARN:"+properties.get('ARM_CREDENTIAL_ID')+" not found...!!!"+"ACCESS_TOKEN should pass in "+EnvironmentFile+"ACCESS_TOKEN="+properties.get('ACCESS_TOKEN')
	}
}

pipeline {
	agent none
	options { timestamps () }
	parameters {
		choice(name: 'EnvironmentFile', choices: [
			'',
			'jenkins-pipeline/external-team/ENV_G_26JUN20/IF-Environment.properties'
		], description: 'Core:in2npdvmuso02\n Custom:in2npdvelk02\n Test env:MD1NPDVINT08')
		booleanParam(defaultValue: false, description: '', name: 'CPS_SRS_MS_MRT_Install')
		booleanParam(defaultValue: false, description: '', name: 'CPS_Deploy')
		booleanParam(defaultValue: false, description: '', name: 'SRS_Deploy')
		booleanParam(defaultValue: false, description: '', name: 'MS_Deploy')
		booleanParam(defaultValue: false, description: '', name: 'TMA_MB_WMA_MRT_Install')
		booleanParam(defaultValue: false, description: '', name: 'TMA_Deploy')
		booleanParam(defaultValue: false, description: '', name: 'MB_Deploy')
		booleanParam(defaultValue: false, description: '', name: 'WMA_Deploy')
		string(name: 'Comment', defaultValue: 'Build Admin', description: 'Please provide reason to trigger build', trim: true )
	}
	stages {
		stage('CPS_SRS_MS_MRT_Install') {
			when {expression { CPS_SRS_MS_MRT_Install == "true" }}
			steps {script {propertiesUtil.install_MRT('CPS')}}}

		stage('CPS_Deploy') {
			when {expression { CPS_Deploy == "true" }}
			steps {script {propertiesUtil.deployApplication('CPS')}}}
			
			stage('SRS_Deploy') {
			when {expression { SRS_Deploy == "true" }}
			steps {script {propertiesUtil.deployApplication('SRS')}}}
			
			stage('MS_Deploy') {
			when {expression { MS_Deploy == "true" }}
			steps {script {propertiesUtil.deployApplication('MS')}}}
		
				stage('TMA_MB_WMA_MRT_Install') {
			when {expression { TMA_MB_WMA_MRT_Install == "true" }}
			steps {script {propertiesUtil.install_MRT('TMA')}}}

		stage('TMA_Deploy') {
			when {expression { TMA_Deploy == "true" }}
			steps {script {propertiesUtil.deployApplication('TMA')}}}
			
			stage('MB_Deploy') {
			when {expression { MB_Deploy == "true" }}
			steps {script {propertiesUtil.deployApplication('MB')}}}
			
			stage('WMA_Deploy') {
			when {expression { WMA_Deploy == "true" }}
			steps {script {propertiesUtil.deployApplication('WMA')}}}
	}
}
