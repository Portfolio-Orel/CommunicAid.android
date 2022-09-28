package com.orels.domain.interactors

interface AnalyticsInteractor {
    fun track(identifier: AnalyticsIdentifiers, value: Map<String, Any> = emptyMap())
    fun track(identifier: AnalyticsIdentifiers, values: List<Map<String, Any>>)
}

enum class AnalyticsIdentifiers(val identifier: String) {
    /* Main */
    ShowMainScreen("user navigated to main sreen"),
    SelectFolderClick("user clicked a folder"),
    FoldersDropdownClick("user clicked the folders dropdown"),
    EditFolderClick("user clicked on edit folder"),
    MessageClickOnCall("user clicked a message when on a call"),
    MessageClickNotOnCall("user clicked a message when not on a call"),
    MessageLongClickOnCall("user long clicked a message when on a call"),
    MessageLongNotOnCall("user long clicked a message when not on a call"),
    /* Main */

    /* Statistics */
    StatisticsScreenShow("user navigated to statistics screen"),
    StatisticsScreenShowWeeks("user chose to see weeks statistics"),
    StatisticsScreenShowMonths("user chose to see months statistics"),
    StatisticsScreenShowAll("user chose to see all statistics"),
    /* Statistics */

    /* Unhandled calls */
    UnhandledCallsShow("user navigated to unhandled calls screen"),
    UnhandledCallsRefresh("user refreshed unhandled calls screen"),
    /* Unhandled calls */

    /* Settings */
    SettingsScreenShow("user navigated to settings screen"),
    SettingsScreenLeftWithoutSave("user changes settings and left without saving it"),
    /* Settings */

}