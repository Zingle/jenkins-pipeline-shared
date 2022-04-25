#!/usr/bin/env groovy
import groovy.json.JsonOutput

/**
 * Send notifications based on build status string
 */
def call(String channel = '#zingle-deployment-notifs', String stage = 'BUILD', String status = 'STARTED') {
  // build status of null means successful
  status =  status ?: 'SUCCESSFUL'

  // Default values
  def color = 'RED'
  def colorCode = '#FF0000'
  def summary = """*${stage}* ${status}: Job `${env.JOB_NAME}` <${env.BUILD_URL}|#${env.BUILD_NUMBER}>

  <${env.CHANGE_URL}|*${env.CHANGE_TITLE}*>"""

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
