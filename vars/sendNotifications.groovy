#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 */
def call(String channel = '#zingle-deployment-notifs', String type = 'BUILD', String status = 'STARTED') {
  // build status of null means successful
  status =  status ?: 'SUCCESSFUL'

  // Default values
  def color = 'RED'
  def colorCode = '#FF0000'
  def summary = """${type} ${status}: Job `${env.JOB_NAME}` [${env.BUILD_NUMBER}](${env.BUILD_URL})

  [${env.CHANGE_TITLE}](${env.CHANGE_URL})"""

  // Override default values based on build status
  if (status == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (status == 'SUCCESSFUL') {
    color = 'GREEN'
    colorCode = '#00FF00'
  }

  // Send notifications
  slackSend (channel: channel, color: colorCode, message: summary)

}
