/**
 * Created by widar on 2016-02-16.
 */

package se.geecity.minecirclejerk

import groovyx.net.http.HTTPBuilder

@Grapes(
        @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
)



class CirclejerkMiner {

    def http

    public CirclejerkMiner() {
        http = new HTTPBuilder("https://www.reddit.com")
    }

    def mineCirclejerk() {

    }
}
