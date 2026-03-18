package yambol.ui.home.models

// Added position for compatibility with HomeViewModel constructor calls
data class PlayerUiModel(
    val name: String,
    val number: String,
    val id: String = "",
    val teamId: String, // This should be only in the domainPlayerModel
)
