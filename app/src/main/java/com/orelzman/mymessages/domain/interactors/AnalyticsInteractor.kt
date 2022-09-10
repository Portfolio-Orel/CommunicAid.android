package com.orelzman.mymessages.domain.interactors

interface AnalyticsInteractor {
    fun track(identifier: AnalyticsIdentifiers, value: Map<String, Any>)
    fun track(identifier: AnalyticsIdentifiers, values: List<Map<String, Any>>)
}

enum class AnalyticsIdentifiers(val identifier: String) {
    /* Main */
    SelectFolderClick("user clicks a folder"),
    FoldersDropdownClick("user clicked the folders dropdown"),
    EditFolderClick("user clicks on edit folder"),
    MessageClickOnCall("user clicked a message when on a call"),
    MessageClickNotOnCall("user clicked a message when not on a call"),
    MessageLongClickOnCall("user long clicked a message when on a call"),
    MessageLongNotOnCall("user long clicked a message when not on a call"),
    /* Main */

    /* Statistics */
    StatisticsScreenShow("user navigated to statistics screen"),
    StatisticsScreenShowMonths("user chose to see months statistics"),
    StatisticsScreenShowAll("user chose to see all statistics"),
    /* Statistics */
}