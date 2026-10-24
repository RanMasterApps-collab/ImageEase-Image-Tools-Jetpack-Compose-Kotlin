@Composable
fun ResizeScreen(
    onBack: () -> Unit,
    onResult: (String) -> Unit,
    viewModel: ResizeViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    val pickImage = {
        pickMedia.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),

        topBar = {
            ToolTopBar(
                title = "Resize Image",
                onBack = onBack,
            )
        },

        bottomBar = {
                Surface(
                    color = Color(0xFFF8F9FF),
                    shadowElevation = 0.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 43.dp,
                            ),
                    ) {
                        if (state.imageInfo != null) {
                        Button(
                            onClick = {
                                viewModel.resize(onResult)
                            },
                            enabled = state.canResize,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(17.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF665DF5),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFFB9B6E8),
                            ),
                        ) {
                            if (state.isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(21.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "Resizing...",
                                    fontWeight = FontWeight.SemiBold,
                                )
                            } else {
                                Text(
                                    text = "Resize Image",
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }


            else{
                    Button(
                        onClick = {
                            pickMedia.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1EBEB0),
                            contentColor = Color.White,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddPhotoAlternate,
                            contentDescription = null,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Select Image",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }


                }}
        }
        },
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {

            // -----------------------------------------------------
            // HEADER
            // -----------------------------------------------------

            Spacer(modifier = Modifier.height(10.dp))
            if (state.imageInfo == null) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),
                    contentAlignment = Alignment.Center,
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.resize
                        ),
                        contentDescription = "Image resize illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Change image dimensions while keeping your image quality.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp,
                    ),
                    color = Color(0xFF69728D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                )

                Spacer(modifier = Modifier.height(22.dp))
            }
            // -----------------------------------------------------
            // NO IMAGE SELECTED
            // -----------------------------------------------------

            if (state.imageInfo == null) {


                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }

            } else {

                val image = state.imageInfo!!

                // -------------------------------------------------
                // IMAGE PREVIEW
                // -------------------------------------------------

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                    ) {

                        ImagePreviewCard(
                            uri = image.uri,
                            contentDescription = "Selected image",
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "Current size",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF8A91A8),
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = FormatUtils.formatDimensions(
                                        image.widthPx,
                                        image.heightPx,
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = Color(0xFF18234A),
                                )
                            }

                            TextButton(
                                onClick = pickImage,
                            ) {
                                Text(
                                    text = "Change",
                                    color = Color(0xFF665DF5),
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }

                // -------------------------------------------------
                // FILE INFO
                // -------------------------------------------------

                Spacer(modifier = Modifier.height(18.dp))

                FileInfoRow(
                    items = listOf(
                        FileInfoItem(
                            "Dimensions",
                            FormatUtils.formatDimensions(
                                image.widthPx,
                                image.heightPx,
                            ),
                        ),
                        FileInfoItem(
                            "File size",
                            FormatUtils.formatBytes(image.sizeBytes),
                        ),
                        FileInfoItem(
                            "Format",
                            image.format?.label ?: "Unknown",
                        ),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                // -------------------------------------------------
                // PRESETS
                // -------------------------------------------------

                ResizeSectionTitle(
                    title = "Quick presets",
                    modifier = Modifier.padding(top = 24.dp),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ResizePresets.forEach { preset ->

                        val selected =
                            state.selectedPresetLabel == preset.label

                        FilterChip(
                            selected = selected,
                            onClick = {
                                viewModel.onPresetSelected(preset)
                            },
                            label = {
                                Text(
                                    "${preset.label}  ${preset.width}×${preset.height}",
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE9E7FF),
                                selectedLabelColor = Color(0xFF665DF5),
                                // Deselected / Unselected
                                containerColor = Color(0xFFF7F7FC),
                                labelColor = Color(0xFF5F6070),  ),
                        )
                    }
                }

                // -------------------------------------------------
                // CUSTOM DIMENSIONS
                // -------------------------------------------------

                ResizeSectionTitle(
                    title = "Custom dimensions",
                    modifier = Modifier.padding(top = 24.dp),
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                ) {
                    DimensionInput(
                        width = state.widthText,
                        height = state.heightText,
                        onWidthChange = viewModel::onWidthChanged,
                        onHeightChange = viewModel::onHeightChanged,
                        lockAspectRatio = state.lockAspectRatio,
                        onLockToggle = viewModel::onLockToggle,
                    )
                }

                // -------------------------------------------------
                // FIT MODE
                // -------------------------------------------------

                ResizeSectionTitle(
                    title = "Fit mode",
                    modifier = Modifier.padding(top = 24.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ResizeModeChip(
                        text = "Fit",
                        selected = state.fitMode == FitMode.FIT,
                        onClick = {
                            viewModel.onFitModeChanged(FitMode.FIT)
                        },
                        modifier = Modifier.weight(1f),
                    )

                    ResizeModeChip(
                        text = "Fill",
                        selected = state.fitMode == FitMode.FILL,
                        onClick = {
                            viewModel.onFitModeChanged(FitMode.FILL)
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                // -------------------------------------------------
                // ADVANCED
                // -------------------------------------------------

                TextButton(
                    onClick = viewModel::onToggleAdvanced,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(
                        text = if (state.showAdvanced)
                            "Hide advanced options"
                        else
                            "Advanced options",
                        color = Color(0xFF665DF5),
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                if (state.showAdvanced) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                        ) {

                            FilterChip(
                                selected =
                                    state.fitMode == FitMode.STRETCH,
                                onClick = {
                                    viewModel.onFitModeChanged(
                                        FitMode.STRETCH
                                    )
                                },
                                label = {
                                    Text(
                                        "Stretch (may distort image)"
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Output format",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF18234A),
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(
                                        rememberScrollState()
                                    ),
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp),
                            ) {

                                FilterChip(
                                    selected = state.outputFormat == null,
                                    onClick = {
                                        viewModel.onOutputFormatChanged(null)
                                    },
                                    label = {
                                        Text("Original")
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                )

                                ImageFormat.entries.forEach { format ->

                                    FilterChip(
                                        selected =
                                            state.outputFormat == format,
                                        onClick = {
                                            viewModel.onOutputFormatChanged(
                                                format
                                            )
                                        },
                                        label = {
                                            Text(format.label)
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------
                // SIZE COMPARISON
                // -------------------------------------------------

                if (
                    state.outputWidth != null &&
                    state.outputHeight != null
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEEEAFE),
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement =
                                Arrangement.SpaceEvenly,
                            verticalAlignment =
                                Alignment.CenterVertically,
                        ) {

                            DimensionResult(
                                label = "Original",
                                value =
                                    FormatUtils.formatDimensions(
                                        image.widthPx,
                                        image.heightPx,
                                    ),
                            )

                            Text(
                                text = "→",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF665DF5),
                                fontWeight = FontWeight.Bold,
                            )

                            DimensionResult(
                                label = "New size",
                                value =
                                    FormatUtils.formatDimensions(
                                        state.outputWidth!!,
                                        state.outputHeight!!,
                                    ),
                                highlighted = true,
                            )
                        }
                    }
                }

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }

    if (state.isProcessing) {
        ProcessingOverlay(
            message = "Resizing image…"
        )
    }
}
