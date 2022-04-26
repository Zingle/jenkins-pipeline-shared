#!/usr/bin/env groovy
import groovy.json.JsonOutput

/**
 * Send notifications based on build status string
 */
def call(options) {
  try {

    if (options.channel){
        channel = options.channel
    } else {
        throw Exception("Slack channel not specified. Please specify a channel when calling function.")
    }

    commitMessage = getCommitMessage()

    def footer = getFooter()
    def text = getText()

    if(options.text_postfix) {
        text = text + " ${options.text_postfix}"
    }

    if(options.footer_postfix) {
        footer = footer + " ${options.footer_postfix}"
    }

    def attachments = JsonOutput.toJson([
        [
            "color": options.color,
            "title": getTitle(),
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

def getTitle() {
  return "${options.icon} ${env.BRANCH_NAME} build #: ${currentBuild.number} ${options.status} "
}

def getFooter() {
  return "[<${env.CHANGE_URL}|${env.BRANCH_NAME}>] [<${env.BUILD_URL}|build #: ${currentBuild.number}>] [target: ${CHANGE_TARGET}]"
}

def getText() {
  return "${commitMessage} author: @${CHANGE_AUTHOR} \n[<${env.BUILD_URL}console|Jenkins Log>] [<${env.BUILD_URL}artifact|Build Artifacts>]"
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
