
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
		], description: '')
		choice(name: 'MRT_Action', choices: [
			'status',
			'start',
			'stop' ,
			'restart' ,
			'remove',
			'install',
			'stop_removeService',
			'installService_start',
			'stop_cleanmule_start',
			'stop_backupmule_start',
			'stop_backuplogs_start',
			'stop_backuplogsmule_start',
			'delete_server_from_ARM',
			'status_server_from_ARM',
			'updateCertificate',
			'stop_remove_delete_serverARM'
		], description: 'Choose Server action')
		booleanParam(defaultValue: false, description: '', name: 'CPS_MS_SRS')
		booleanParam(defaultValue: false, description: '', name: 'TMA_MB_WMA')
		string(name: 'Comment', defaultValue: 'Build Admin', description: 'Please provide reason to trigger build', trim: true )
	}
	stages {
		stage('CPS_MS_SRS') {
			when {expression { CPS_MS_SRS == "true" }}
			steps {script {propertiesUtil.MRT_Action_job('CPS')}}}
			
		stage('TMA_MB_WMA') {
			when {expression { TMA_MB_WMA == "true" }}
			steps {script {propertiesUtil.MRT_Action_job('TMA')}}}

	}
}
