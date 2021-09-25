package net.xblacky.animexstream.utils.parser

import net.xblacky.animexstream.utils.constants.C
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import timber.log.Timber


class HtmlParserTest {

    //Check Parsing of HTML Contents
    @Test
    //Give a static response from network & Checks Parser parses data normally
    fun parseRecentSub_StaticResponse_ListOfAnimeMetaModel() {

        //Given Data
        val networkResponse = NetworkResponse.recentSubResponse

        //Processing Data
        val list = HtmlParser.parseRecentSubOrDub(networkResponse, C.TYPE_RECENT_SUB)
        println(list.toString())
        //Check Response
        assertThat(list.size, `is`(20))

    }

    @Test
    //Give a static response from network & Checks Parser parses data normally
    fun parseRecentSub_emptyResponse_Error() {

        //Given Data
        val networkResponse = ""
        //Processing Data
        val list = HtmlParser.parseRecentSubOrDub(networkResponse, C.TYPE_RECENT_SUB)

        //Check Response
        assertThat(list.size, `is`(0))

    }

    @Test
    //Give a static response from network & Checks Parser parses data normally
    fun parseRecentSub_InstanceCreation_NotNUll() {

        val networkResponse = null
        //Processing Data
        val list = HtmlParser.parseRecentSubOrDub(networkResponse, C.TYPE_RECENT_SUB)

        //Check Response
        assertThat(list, `is`(nullValue()))

    }

}