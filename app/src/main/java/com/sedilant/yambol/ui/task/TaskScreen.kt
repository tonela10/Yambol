package com.sedilant.yambol.ui.task

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.R
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.ConceptDto
import com.sedilant.yambol.data.firestore.ConceptRepository
import com.sedilant.yambol.domain.UpdateTaskUseCase
import com.sedilant.yambol.domain.delete.DeleteTaskUseCase
import com.sedilant.yambol.domain.get.GetTaskByIdUseCase
import com.sedilant.yambol.domain.get.GetTaskConceptNameUseCase
import com.sedilant.yambol.domain.get.IsTaskUsedByAnyTrainUseCase
import com.sedilant.yambol.domain.models.TaskDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun TaskScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    taskViewModel: TaskViewModel = hiltViewModel(
        creationCallback = { factory: TaskViewModelFactory ->
            factory.create(taskId)
        }
    )
) {
    val uiState by taskViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if ((uiState as? TaskUiState.Success)?.isDeleted == true) {
            onNavigateBack()
        }
    }

    TaskScreenStateless(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onEdit = taskViewModel::onEdit,
        onNameChange = taskViewModel::onNameChange,
        onDescriptionChange = taskViewModel::onDescriptionChange,
        onVariablesChange = taskViewModel::onVariablesChange,
        onToggleConcept = taskViewModel::onToggleConcept,
        onShowAddConceptDialog = taskViewModel::onShowAddConceptDialog,
        onDismissAddConceptDialog = taskViewModel::onDismissAddConceptDialog,
        onNewConceptInputChange = taskViewModel::onNewConceptInputChange,
        onCreateConcept = taskViewModel::onCreateConcept,
        onDelete = taskViewModel::onDelete,
        onDismissDeleteDialog = taskViewModel::onDismissDeleteDialog,
        onConfirmDelete = taskViewModel::onConfirmDelete,
        onCancelEdit = taskViewModel::onCancelEdit,
        onSave = taskViewModel::onSave,
        onRetry = taskViewModel::load,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskScreenStateless(
    uiState: TaskUiState,
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onVariablesChange: (String) -> Unit,
    onToggleConcept: (String) -> Unit,
    onShowAddConceptDialog: () -> Unit,
    onDismissAddConceptDialog: () -> Unit,
    onNewConceptInputChange: (String) -> Unit,
    onCreateConcept: () -> Unit,
    onDelete: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelEdit: () -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
) {
    TaskScreenSystemBars()

    Scaffold(
        containerColor = TaskScreenPalette.Background,
    ) { paddingValues ->
        when (uiState) {
            TaskUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = TaskScreenPalette.Accent,
                    )
                }
            }

            is TaskUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(uiState.messageResId),
                        color = TaskScreenPalette.Error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.padding(top = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TaskScreenPalette.Accent,
                            contentColor = Color.White,
                        )
                    ) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
            }

            is TaskUiState.Success -> {
                if (uiState.isDeleteDialogVisible) {
                    AlertDialog(
                        onDismissRequest = onDismissDeleteDialog,
                        title = { Text(stringResource(R.string.delete_task_confirmation_title)) },
                        text = {
                            Text(
                                text = stringResource(
                                    if (uiState.isTaskUsedByAnyTrain) {
                                        R.string.delete_task_used_warning
                                    } else {
                                        R.string.delete_task_confirmation_message
                                    }
                                )
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = onConfirmDelete,
                                enabled = !uiState.isDeleting,
                            ) {
                                Text(
                                    text = stringResource(R.string.delete),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = onDismissDeleteDialog) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }

                if (uiState.isAddConceptDialogVisible) {
                    AlertDialog(
                        onDismissRequest = onDismissAddConceptDialog,
                        title = { Text(text = stringResource(R.string.add_concept)) },
                        text = {
                            OutlinedTextField(
                                value = uiState.newConceptInput,
                                onValueChange = onNewConceptInputChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(text = stringResource(R.string.new_concept_name)) },
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = onCreateConcept) {
                                Text(stringResource(R.string.create_concept))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = onDismissAddConceptDialog) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }

                val selectedConceptNames = rememberSelectedConceptNames(uiState)
                val variants = uiState.variablesInput
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(TaskScreenPalette.Background)
                        .verticalScroll(rememberScrollState()),
                ) {
                    TaskTopBar(
                        isEditing = uiState.isEditing,
                        onNavigateBack = onNavigateBack,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        title = uiState.name,
                    )

                    TaskMainCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                    ) {
                        if (uiState.isEditing) {
                            TaskSectionLabel(title = stringResource(R.string.task_description))
                            Spacer(modifier = Modifier.height(10.dp))

                            TaskEditSection {
                                DarkOutlinedField(
                                    value = uiState.name,
                                    onValueChange = onNameChange,
                                    label = stringResource(R.string.task_name),
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                DarkOutlinedField(
                                    value = uiState.description,
                                    onValueChange = onDescriptionChange,
                                    label = stringResource(R.string.task_description),
                                    minLines = 4,
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                DarkOutlinedField(
                                    value = uiState.variablesInput,
                                    onValueChange = onVariablesChange,
                                    label = stringResource(R.string.task_variables_hint),
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            TaskSectionLabel(title = stringResource(R.string.task_skills_targeted))
                            Spacer(modifier = Modifier.height(10.dp))

                            TaskEditSection {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    uiState.availableConcepts.forEachIndexed { index, concept ->
                                        PastelSelectableConceptChip(
                                            text = concept.name,
                                            selected = uiState.conceptIds.contains(concept.id),
                                            paletteIndex = index,
                                            onClick = { onToggleConcept(concept.id) },
                                        )
                                    }
                                }

                                TextButton(onClick = onShowAddConceptDialog) {
                                    Text(text = stringResource(R.string.add_concept))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(onClick = onCancelEdit) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                                Button(
                                    onClick = onSave,
                                    enabled = !uiState.isSaving,
                                    modifier = Modifier.padding(start = 8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TaskScreenPalette.Accent,
                                        contentColor = Color.White,
                                    )
                                ) {
                                    Text(text = stringResource(R.string.save))
                                }
                            }
                        } else {
                            TaskSectionLabel(title = stringResource(R.string.task_description))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = uiState.description.ifBlank { stringResource(R.string.task_no_notes) },
                                style = MaterialTheme.typography.headlineSmall,
                                color = TaskScreenPalette.TextPrimary,
                                lineHeight = MaterialTheme.typography.headlineSmall.lineHeight,
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            TaskSectionLabel(title = stringResource(R.string.task_skills_targeted))
                            Spacer(modifier = Modifier.height(12.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                if (selectedConceptNames.isEmpty()) {
                                    EmptyInlineHint(text = stringResource(R.string.task_no_concepts))
                                } else {
                                    selectedConceptNames.forEachIndexed { index, conceptName ->
                                        PastelConceptChip(
                                            text = conceptName,
                                            paletteIndex = index,
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            TaskSectionLabel(title = stringResource(R.string.task_variants))
                            Spacer(modifier = Modifier.height(12.dp))
                            if (variants.isEmpty()) {
                                EmptyInlineHint(text = stringResource(R.string.task_no_variants))
                            } else {
                                variants.forEachIndexed { index, variant ->
                                    TaskVariantRow(
                                        index = index + 1,
                                        text = variant,
                                    )
                                    if (index != variants.lastIndex) {
                                        Spacer(modifier = Modifier.height(18.dp))
                                    }
                                }
                            }
                        }

                        uiState.feedbackMessage?.let { message ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = TaskScreenPalette.Accent.copy(alpha = 0.18f),
                            ) {
                                Text(
                                    text = stringResource(message),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TaskScreenPalette.TextPrimary,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 14.dp
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberSelectedConceptNames(uiState: TaskUiState.Success): List<String> {
    val conceptNameById = uiState.availableConcepts.associate { it.id to it.name }
    return uiState.conceptIds.mapNotNull { conceptNameById[it] }
}

@Composable
private fun TaskTopBar(
    isEditing: Boolean,
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    title: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            TaskHeaderActionButton(
                onClick = onNavigateBack,
                iconTint = TaskScreenPalette.TextPrimary,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = TaskScreenPalette.TextPrimary,
                    )
                }
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TaskScreenPalette.TextPrimary,
            )
        }

        if (!isEditing) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskHeaderActionButton(
                    onClick = onEdit,
                    iconTint = TaskScreenPalette.Action,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_task),
                            tint = TaskScreenPalette.Action,
                        )
                    }
                )
                TaskHeaderActionButton(
                    onClick = onDelete,
                    iconTint = TaskScreenPalette.Error,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_task),
                            tint = TaskScreenPalette.Error,
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TaskHeaderActionButton(
    onClick: () -> Unit,
    iconTint: Color,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, TaskScreenPalette.ButtonBorder, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick) {
            icon()
        }
    }
}

@Composable
private fun TaskSectionLabel(title: String) {
    Text(
        text = title.uppercase(Locale.getDefault()),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = TaskScreenPalette.SectionLabel,
    )
}

@Composable
private fun TaskMainCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(36.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content,
        )
    }
}

@Composable
private fun TaskEditSection(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = TaskScreenPalette.SectionSurface,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content,
        )
    }
}

@Composable
private fun TaskVariantRow(
    index: Int,
    text: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = TaskScreenPalette.NumberCircle,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = index.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = TaskScreenPalette.TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.task_variant_supporting_text),
                style = MaterialTheme.typography.bodyLarge,
                color = TaskScreenPalette.TextSecondary,
            )
        }
    }
}

@Composable
private fun EmptyInlineHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = TaskScreenPalette.TextSecondary,
    )
}

@Composable
private fun PastelConceptChip(
    text: String,
    paletteIndex: Int,
) {
    val palette = conceptChipPalette(paletteIndex)
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = palette.background,
        modifier = Modifier.border(1.dp, palette.border, RoundedCornerShape(24.dp)),
    ) {
        Text(
            text = text,
            color = palette.content,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun PastelSelectableConceptChip(
    text: String,
    selected: Boolean,
    paletteIndex: Int,
    onClick: () -> Unit,
) {
    val palette = conceptChipPalette(paletteIndex)
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = palette.background,
            labelColor = palette.content,
            selectedContainerColor = palette.content,
            selectedLabelColor = Color.White,
        )
    )
}

private data class ConceptChipPalette(
    val background: Color,
    val border: Color,
    val content: Color,
)

private fun conceptChipPalette(index: Int): ConceptChipPalette = when (index % 3) {
    0 -> ConceptChipPalette(
        background = Color(0xFFFFF3E8),
        border = Color(0xFFFFD8B0),
        content = Color(0xFFFF6A00),
    )

    1 -> ConceptChipPalette(
        background = Color(0xFFEAF3FF),
        border = Color(0xFFC9DDFF),
        content = Color(0xFF2D68FF),
    )

    else -> ConceptChipPalette(
        background = Color(0xFFEAFBF0),
        border = Color(0xFFC8F0D4),
        content = Color(0xFF18A957),
    )
}

@Composable
private fun DarkOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TaskScreenPalette.TextPrimary,
            unfocusedTextColor = TaskScreenPalette.TextPrimary,
            focusedContainerColor = TaskScreenPalette.Field,
            unfocusedContainerColor = TaskScreenPalette.Field,
            focusedBorderColor = TaskScreenPalette.Accent,
            unfocusedBorderColor = TaskScreenPalette.CardOutline,
            focusedLabelColor = TaskScreenPalette.AccentSoft,
            unfocusedLabelColor = TaskScreenPalette.TextSecondary,
            cursorColor = TaskScreenPalette.Accent,
        )
    )
}

@Composable
private fun TaskScreenSystemBars() {
    val view = LocalView.current
    if (view.isInEditMode) return

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        val background = TaskScreenPalette.Background.toArgb()
        window.statusBarColor = background
        window.navigationBarColor = background
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }
}

private object TaskScreenPalette {
    val Background = Color(0xFFF4F6FA)
    val SectionSurface = Color(0xFFF7F8FC)
    val Field = Color(0xFFF7F8FC)
    val CardOutline = Color(0xFFD9DEE8)
    val TextPrimary = Color(0xFF1C2A44)
    val TextSecondary = Color(0xFF6E788A)
    val SectionLabel = Color(0xFF9CA3AF)
    val Accent = Color(0xFF4C6FFF)
    val AccentSoft = Color(0xFF7B8DFF)
    val Action = Color(0xFF98A2B3)
    val ButtonBorder = Color(0xFFE1E6EF)
    val NumberCircle = Color(0xFF1A1D22)
    val Error = Color(0xFFFF6B6B)
}

@HiltViewModel(assistedFactory = TaskViewModelFactory::class)
class TaskViewModel @AssistedInject constructor(
    @Assisted private val taskId: String,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getTaskConceptNameUseCase: GetTaskConceptNameUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val isTaskUsedByAnyTrainUseCase: IsTaskUsedByAnyTrainUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val conceptRepository: ConceptRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private var sourceTask: TaskDomain? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading

            val task = getTaskByIdUseCase(taskId)
            if (task == null) {
                _uiState.value = TaskUiState.Error(R.string.task_not_found)
                return@launch
            }

            sourceTask = task
            val conceptNameById = getTaskConceptNameUseCase(task.concepts)
            val conceptNames = task.concepts.mapNotNull { conceptNameById[it] }
            val availableConcepts = loadAvailableConcepts()

            _uiState.value = TaskUiState.Success(
                taskId = task.trainingTaskId,
                name = task.name,
                description = task.description,
                conceptIds = task.concepts,
                conceptNames = conceptNames,
                availableConcepts = availableConcepts,
                variablesInput = task.variables.joinToString(", "),
            )
        }
    }

    private suspend fun loadAvailableConcepts(): List<ConceptOption> {
        val userId = authRepository.currentUser?.uid ?: return emptyList()
        return conceptRepository
            .listByUserFlow(userId)
            .first()
            .mapNotNull { concept ->
                val name = concept.name.trim()
                if (name.isBlank()) null else ConceptOption(concept.id, name)
            }
            .sortedBy { it.name.lowercase() }
    }

    fun onEdit() {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(isEditing = true, feedbackMessage = null)
        }
    }

    fun onCancelEdit() {
        val task = sourceTask ?: return
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(
                name = task.name,
                description = task.description,
                variablesInput = task.variables.joinToString(", "),
                isEditing = false,
                isSaving = false,
                feedbackMessage = null,
            )
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(name = newName, feedbackMessage = null)
        }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(description = newDescription, feedbackMessage = null)
        }
    }

    fun onVariablesChange(newVariables: String) {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(variablesInput = newVariables, feedbackMessage = null)
        }
    }

    fun onToggleConcept(conceptId: String) {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            val newConceptIds = if (successState.conceptIds.contains(conceptId)) {
                successState.conceptIds.filterNot { it == conceptId }
            } else {
                successState.conceptIds + conceptId
            }
            successState.copy(conceptIds = newConceptIds, feedbackMessage = null)
        }
    }

    fun onShowAddConceptDialog() {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(isAddConceptDialogVisible = true, newConceptInput = "")
        }
    }

    fun onDismissAddConceptDialog() {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(isAddConceptDialogVisible = false, newConceptInput = "")
        }
    }

    fun onNewConceptInputChange(newValue: String) {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(newConceptInput = newValue)
        }
    }

    fun onCreateConcept() {
        viewModelScope.launch {
            val state = _uiState.value as? TaskUiState.Success ?: return@launch
            val conceptName = state.newConceptInput.trim()
            if (conceptName.isBlank()) {
                _uiState.update {
                    (it as? TaskUiState.Success)?.copy(
                        feedbackMessage = R.string.new_concept_name_empty
                    ) ?: it
                }
                return@launch
            }

            val userId = authRepository.currentUser?.uid ?: return@launch
            val conceptId = conceptRepository.upsert(
                ConceptDto(
                    id = "",
                    name = conceptName,
                    userId = userId,
                )
            )

            val refreshedConcepts = loadAvailableConcepts()
            _uiState.update {
                (it as? TaskUiState.Success)?.copy(
                    availableConcepts = refreshedConcepts,
                    conceptIds = (it.conceptIds + conceptId).distinct(),
                    isAddConceptDialogVisible = false,
                    newConceptInput = "",
                    feedbackMessage = null,
                ) ?: it
            }
        }
    }

    fun onDelete() {
        viewModelScope.launch {
            val state = _uiState.value as? TaskUiState.Success ?: return@launch
            _uiState.update {
                (it as? TaskUiState.Success)?.copy(isCheckingUsage = true, feedbackMessage = null)
                    ?: it
            }

            val isUsed = isTaskUsedByAnyTrainUseCase(state.taskId)

            _uiState.update {
                (it as? TaskUiState.Success)?.copy(
                    isCheckingUsage = false,
                    isTaskUsedByAnyTrain = isUsed,
                    isDeleteDialogVisible = true,
                ) ?: it
            }
        }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { state ->
            val successState = state as? TaskUiState.Success ?: return@update state
            successState.copy(
                isDeleteDialogVisible = false,
                isTaskUsedByAnyTrain = false,
                isDeleting = false,
            )
        }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            val state = _uiState.value as? TaskUiState.Success ?: return@launch
            _uiState.update {
                (it as? TaskUiState.Success)?.copy(isDeleting = true) ?: it
            }

            deleteTaskUseCase(state.taskId)

            _uiState.update {
                (it as? TaskUiState.Success)?.copy(
                    isDeleting = false,
                    isDeleteDialogVisible = false,
                    isDeleted = true,
                    feedbackMessage = R.string.task_deleted,
                ) ?: it
            }
        }
    }

    fun onSave() {
        viewModelScope.launch {
            val state = _uiState.value as? TaskUiState.Success ?: return@launch
            if (state.name.isBlank()) {
                _uiState.update {
                    (it as? TaskUiState.Success)?.copy(
                        feedbackMessage = R.string.task_name_empty
                    ) ?: it
                }
                return@launch
            }

            _uiState.update {
                (it as? TaskUiState.Success)?.copy(isSaving = true, feedbackMessage = null) ?: it
            }

            val variables = state.variablesInput
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            updateTaskUseCase(
                taskId = state.taskId,
                name = state.name,
                description = state.description,
                variables = variables,
                conceptIds = state.conceptIds,
            )

            sourceTask = TaskDomain(
                trainingTaskId = state.taskId,
                name = state.name,
                concepts = state.conceptIds,
                description = state.description,
                variables = variables,
            )

            _uiState.update {
                (it as? TaskUiState.Success)?.copy(
                    isEditing = false,
                    isSaving = false,
                    feedbackMessage = R.string.task_updated,
                ) ?: it
            }
        }
    }
}

data class ConceptOption(
    val id: String,
    val name: String,
)

sealed interface TaskUiState {
    data object Loading : TaskUiState
    data class Error(val messageResId: Int) : TaskUiState
    data class Success(
        val taskId: String,
        val name: String,
        val description: String,
        val conceptIds: List<String>,
        val conceptNames: List<String>,
        val availableConcepts: List<ConceptOption>,
        val variablesInput: String,
        val isEditing: Boolean = false,
        val isSaving: Boolean = false,
        val isDeleting: Boolean = false,
        val isCheckingUsage: Boolean = false,
        val isDeleteDialogVisible: Boolean = false,
        val isTaskUsedByAnyTrain: Boolean = false,
        val isAddConceptDialogVisible: Boolean = false,
        val newConceptInput: String = "",
        val isDeleted: Boolean = false,
        val feedbackMessage: Int? = null,
    ) : TaskUiState
}

@AssistedFactory
interface TaskViewModelFactory {
    fun create(taskId: String): TaskViewModel
}

