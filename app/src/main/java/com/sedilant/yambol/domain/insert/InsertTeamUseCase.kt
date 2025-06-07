package com.sedilant.yambol.domain.insert

interface InsertTeamUseCase {
    suspend operator fun invoke(name: String)
}
