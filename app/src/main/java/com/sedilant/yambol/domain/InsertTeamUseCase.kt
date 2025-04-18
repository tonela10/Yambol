package com.sedilant.yambol.domain

interface InsertTeamUseCase {
    suspend operator fun invoke(name: String)
}
