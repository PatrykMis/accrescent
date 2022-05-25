package app.accrescent.client.ui

data class AppDetailsUiState(
    val isFetchingData: Boolean = false,
    val appExists: Boolean = true,
    val appId: String,
    val appName: String = "",
    val versionName: String = "",
    val versionCode: Long = 0,
)