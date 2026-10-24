@Composable
fun PdfScreen(
    onBack: () -> Unit,
    onResult: (String) -> Unit,
    viewModel: PdfViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 30),
    ) { uris ->
        viewModel.onImagesSelected(uris)
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        topBar = {
            ToolTopBar(
                title = "Images to PDF",
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFFF8F9FF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 10.dp,
                            bottom = 43.dp
                        )
                ) {
                    PrimaryActionButton(
                        text = if (state.images.isEmpty()) {
                            "Select Images"
                        } else {
                            "Create PDF"
                        },
                        onClick = {
                            if (state.images.isEmpty()) {
                                pickMedia.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            } else {
                                viewModel.createPdf(onResult)
                            }
                        },
                        enabled = !state.isProcessing,
                        loading = state.isProcessing,
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            // -----------------------------------------------------
            // Header illustration
            // Show only before selecting images
            // -----------------------------------------------------
            if (state.images.isEmpty()) {

                PdfHeaderSection()

                Spacer(modifier = Modifier.height(8.dp))
            }

            // -----------------------------------------------------
            // Selected images + PDF options
            // -----------------------------------------------------
            if (state.images.isNotEmpty()) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Selected Images",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF18245C)
                        )

                        Text(
                            text = "${state.images.size} images • Arrange your pages",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7A819C)
                        )
                    }

                    TextButton(
                        onClick = {
                            pickMedia.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Add More",
                            color = Color(0xFF665DF5),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                PdfThumbnailCard(
                    items = state.images,
                    onMove = viewModel::onMove,
                    onRemove = viewModel::onRemove,
                )

                Spacer(modifier = Modifier.height(22.dp))

                // -------------------------------------------------
                // Page Setup
                // -------------------------------------------------
                PdfSectionTitle("Page Setup")

                PdfOptionRow(
                    options = listOf(
                        "A4" to (state.pageSize == PdfPageSizeOption.A4),
                        "Letter" to (state.pageSize == PdfPageSizeOption.LETTER),
                        "Custom" to (state.pageSize == PdfPageSizeOption.AUTO),
                    ),
                    onSelected = { label ->
                        when (label) {
                            "A4" -> viewModel.onPageSizeChange(
                                PdfPageSizeOption.A4
                            )

                            "Letter" -> viewModel.onPageSizeChange(
                                PdfPageSizeOption.LETTER
                            )
                        "Custom" -> viewModel.onPageSizeChange(
                                PdfPageSizeOption.AUTO
                            )
                        }
                    }
                )
                if (state.pageSize == PdfPageSizeOption.AUTO) {

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        OutlinedTextField(
                            value = state.customWidthMm,
                            onValueChange = viewModel::onCustomWidthChange,
                            label = {
                                Text("Width")
                            },
                            suffix = {
                                Text("mm",        color = Color(0xFF64748B)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD1D5DB),
                                unfocusedBorderColor = Color(0xFFD1D5DB),

                                focusedLabelColor = Color(0xFF475569),
                                unfocusedLabelColor = Color(0xFF475569),

                                focusedPlaceholderColor = Color(0xFF64748B),
                                unfocusedPlaceholderColor = Color(0xFF64748B),

                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),

                                cursorColor = Color(0xFF475569)
                            )
                        )

                        OutlinedTextField(
                            value = state.customHeightMm,
                            onValueChange = viewModel::onCustomHeightChange,
                            label = {
                                Text("Height")
                            },
                            suffix = {
                                Text("mm",        color = Color(0xFF64748B)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD1D5DB),
                                unfocusedBorderColor = Color(0xFFD1D5DB),

                                focusedLabelColor = Color(0xFF475569),
                                unfocusedLabelColor = Color(0xFF475569),

                                focusedPlaceholderColor = Color(0xFF64748B),
                                unfocusedPlaceholderColor = Color(0xFF64748B),

                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),

                                cursorColor = Color(0xFF475569)
                            ))
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))

                // -------------------------------------------------
                // Orientation
                // -------------------------------------------------
                PdfSectionTitle("Orientation")

                PdfOptionRow(
                    options = listOf(
                        "Portrait" to (
                                state.orientation ==
                                        PdfOrientationOption.PORTRAIT
                                ),
                        "Landscape" to (
                                state.orientation ==
                                        PdfOrientationOption.LANDSCAPE
                                ),
                    ),
                    onSelected = { label ->
                        when (label) {
                            "Portrait" -> viewModel.onOrientationChange(
                                PdfOrientationOption.PORTRAIT
                            )

                            "Landscape" -> viewModel.onOrientationChange(
                                PdfOrientationOption.LANDSCAPE
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // -------------------------------------------------
                // Image Fit
                // -------------------------------------------------
                PdfSectionTitle("Image Fit")

                PdfOptionRow(
                    options = listOf(
                        "Fit to page" to (
                                state.fitMode == FitMode.FIT
                                ),
                        "Actual size" to (
                                state.fitMode == FitMode.FILL
                                ),
                    ),
                    onSelected = { label ->
                        when (label) {
                            "Fit to page" -> viewModel.onFitModeChange(
                                FitMode.FIT
                            )

                            "Actual size" -> viewModel.onFitModeChange(
                                FitMode.FILL
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // -------------------------------------------------
                // Margins
                // -------------------------------------------------
                PdfSectionTitle("Margins")

                PdfOptionRow(
                    options = listOf(
                        "None" to (
                                state.margin == PdfMarginOption.NONE
                                ),
                        "Small" to (
                                state.margin == PdfMarginOption.SMALL
                                ),
                        "Medium" to (
                                state.margin == PdfMarginOption.MEDIUM
                                ),
                    ),
                    onSelected = { label ->
                        when (label) {
                            "None" -> viewModel.onMarginChange(
                                PdfMarginOption.NONE
                            )

                            "Small" -> viewModel.onMarginChange(
                                PdfMarginOption.SMALL
                            )

                            "Medium" -> viewModel.onMarginChange(
                                PdfMarginOption.MEDIUM
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // -------------------------------------------------
                // File name
                // -------------------------------------------------
  OutlinedTextField(
                    value = state.fileName,
                    onValueChange = viewModel::onFileNameChange,
                    label = {
                        Text("File Name")
                    },
                    placeholder = {
                        Text("ImageEase_020393939")
                    },
                    suffix = {
                        Text(".pdf",        color = Color(0xFF64748B))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
      colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color(0xFFD1D5DB),
          unfocusedBorderColor = Color(0xFFD1D5DB),

          focusedLabelColor = Color(0xFF475569),
          unfocusedLabelColor = Color(0xFF475569),

          focusedPlaceholderColor = Color(0xFF64748B),
          unfocusedPlaceholderColor = Color(0xFF64748B),

          focusedTextColor = Color(0xFF1E293B),
          unfocusedTextColor = Color(0xFF1E293B),

          cursorColor = Color(0xFF475569)
      ))
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (state.isProcessing) {
        ProcessingOverlay(
            message = "Creating PDF…",
            progress =
                if (state.progressTotal > 0) {
                    state.progressCompleted.toFloat() /
                            state.progressTotal
                } else {
                    null
                },
            progressText =
                if (state.progressTotal > 0) {
                    "${state.progressCompleted} / ${state.progressTotal} images"
                } else {
                    null
                },
        )
    }
}
@Composable
private fun PdfHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.pdf),
            contentDescription = "Images to PDF",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Combine multiple images into a clean,\nwell-formatted PDF document.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF273B73),
            lineHeight = 20.sp
        )
    }
}
@Composable
private fun PdfSectionTitle(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF18245C),
        modifier = Modifier.padding(bottom = 9.dp)
    )
}
@Composable
private fun PdfOptionRow(
    options: List<Pair<String, Boolean>>,
    onSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { (label, selected) ->

            Surface(
                modifier = Modifier.weight(1f),
                onClick = {
                    onSelected(label)
                },
                shape = RoundedCornerShape(16.dp),
                color = if (selected) {
                    Color(0xFF665DF5)
                } else {
                    Color.White
                },
                border = if (!selected) {
                    BorderStroke(
                        1.dp,
                        Color(0xFFE3E6F2)
                    )
                } else {
                    null
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selected) {
                            Color.White
                        } else {
                            Color(0xFF526080)
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun PdfThumbnailCard(
    items: List<PdfImageItem>,
    onMove: (Int, Int) -> Unit,
    onRemove: (PdfImageItem) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        PdfThumbnailRow(
            items = items,
            onMove = onMove,
            onRemove = onRemove,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .heightIn(max = 210.dp)
        )
    }
}
