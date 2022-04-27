#!/usr/bin/env groovy
import groovy.json.JsonOutput

/**
 * Send slack notification with customized options
 */
def call(options) {
  try {

    if (options.channel){
        channel = options.channel
    } else {
        throw Exception("Slack channel not specified. Please specify a channel when calling function.")
    }

    def footer = getFooter(options)
    def text = getText(options)

    def attachments = JsonOutput.toJson([
        [
            "color": getColor(options),
            "title": getTitle(options),
            "text": text,
            "footer": footer
        ]
    ])
    
    if ("true".equals(env.SKIP_SLACK_SEND)) {
      echo "SKIP_SLACK_SEND = true"
    } else {
      slackSend(channel: channel, attachments: attachments)
    }

  } catch (Exception error) {
    echo "sendSlackStatusNotification error ${error}"
  }
}

def getColor (options) {
  color = 'grey'
  if(options.color) {
    color = options.color
  }
  return color
}

def getTitle(options) {
  text = "${options.icon} `${env.JOB_NAME}` build #${currentBuild.number}: ${options.status} "

  if(options.text_postfix) {
      text = text + " ${options.text_postfix}"
  }
  return text

}

def getFooter(options) {
  footer = "[<${env.CHANGE_URL}|${env.BRANCH_NAME}>] [<${env.BUILD_URL}|build #: ${currentBuild.number}>] [target: ${env.BRANCH_NAME}]"

  if(options.footer_postfix) {
      footer = footer + " ${options.footer_postfix}"
  }
  return footer

}

def getText(options) {
  commitMessage = getCommitMessage()
  return "${commitMessage} \n[<${env.BUILD_URL}console|Jenkins Log>] [<${env.BUILD_URL}artifact|Build Artifacts>]"
}

def getCommitMessage() {
    def commitMessage = ""

    try {
        commitMessage = sh(script: 'git log -n2 --pretty=format:"%an: %s [#%h]" | grep -v "Continuous Deliver: Merge branch"; exit 0', returnStdout: true).trim()
    } catch (Exception error) {
        echo "ignore git log error ${error}"
    }

    return commitMessage
}
