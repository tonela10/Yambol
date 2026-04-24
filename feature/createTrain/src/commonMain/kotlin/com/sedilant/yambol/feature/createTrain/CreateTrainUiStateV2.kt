package com.sedilant.yambol.feature.createTrain

data class CreateTrainUiStateV2(
    val currentStep: Step = Step.BASIC_INFO,
    val teamId: String,
)

enum class Step {
    BASIC_INFO,
    CONCEPTS,
    TASKS,
    TRAIN_DETAIL
}
