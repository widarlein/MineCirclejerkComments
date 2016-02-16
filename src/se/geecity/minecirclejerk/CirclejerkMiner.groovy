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
            def comments = getComments(after)
            println comments.size()
            for (comment in comments) {
                def jobName = appendComment(comment.data.body)
                println jobName
                commentCount++
                if (commentCount == commentNumber) {
                    break
                }
            }

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
                return json.data.children
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

