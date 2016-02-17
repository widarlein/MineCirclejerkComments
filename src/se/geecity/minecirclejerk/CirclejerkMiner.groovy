/**
 * Created by widar on 2016-02-16.
 */

package se.geecity.minecirclejerk

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

@Grapes(
        @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
)



class CirclejerkMiner {

    def http
    def internetOnAStickBaseUrl
    def internetOnAStickUser
    def internetOnAStickMarkov = "circlejerk"
    def commentNumber

    public CirclejerkMiner() {
        http = new HTTPBuilder()
    }

    def mineCirclejerk() {
        def commentCount = 0
        def after = null
        while (commentCount < commentNumber) {
            def commentsJson = getComments(after)
            def comments = commentsJson.data.children
            println comments.size()
            for (comment in comments) {
                try {
                def jobName = appendComment(comment.data.body)
                println jobName
                } catch (all) {println "Exception $all"}
                commentCount++
                if (commentCount == commentNumber) {
                    break
                }
            }
            after = commentsJson.data.after

        }
        println "Stopping mining after $commentCount comments"
    }

    private def getComments(after = null) {
        http.request("https://www.reddit.com/r/circlejerk/comments.json", GET, JSON) {
            uri.query = [limit: "100"]

            if (after) {
                uri.query.after = after
            }

            response.success = { resp, json ->
                return json
            }

        }
    }

    private def createMarkov() {

    }

    private def appendComment(comment) {
        http.request("$internetOnAStickBaseUrl/$internetOnAStickUser/$internetOnAStickMarkov/appendLineFile", POST, JSON) {

            requestContentType = JSON

            body = [data: comment.bytes.encodeBase64().toString()]

            response.success = { resp, json ->
                return json.jobName
            }

            response.failure = { resp ->
                println resp
            }
        }
    }


    static void main(String[] args) {
        File configFile = new File("../../../../config.groovy")
        println "File is $configFile.absolutePath and it exists ${configFile.exists()}"
        def configSlurper = new ConfigSlurper()
        def config = configSlurper.parse(configFile.text)

        def miner = new CirclejerkMiner()
        miner.internetOnAStickBaseUrl = config.internetOnAStick.baseUrl
        miner.internetOnAStickUser = config.internetOnAStick.user
        miner.commentNumber = config.commentNumber
        miner.mineCirclejerk()
    }
}

