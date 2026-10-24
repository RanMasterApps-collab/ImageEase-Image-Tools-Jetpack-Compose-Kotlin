@Composable
fun CropScreen(
    onBack: () -> Unit,
    onResult: (String) -> Unit,
    viewModel: CropViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var selectedTransform by remember {
        mutableStateOf<String?>(null)
    }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
            selectedTransform = null
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),

        // ---------------------------------------------------------
        // Top Bar
        // ---------------------------------------------------------
        topBar = {
            ToolTopBar(
                title = "Crop Image",
                onBack = onBack,
            )
        },

        // ---------------------------------------------------------
        // Bottom Action
        // ---------------------------------------------------------
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
                            bottom = 20.dp,
                        )
                ) {

                    if (state.imageInfo == null) {

                        // -------------------------------------------------
                        // Select Image
                        // -------------------------------------------------
                        Button(
                            onClick = {
                                pickMedia.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts
                                            .PickVisualMedia
                                            .ImageOnly
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
                                imageVector =
                                    Icons.Outlined.AddPhotoAlternate,
                                contentDescription = null,
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = "Select Image",
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                    } else {

                        // -------------------------------------------------
                        // Crop Action
                        // -------------------------------------------------
                        PrimaryActionButton(
                            text = "Crop Image",
                            onClick = {
                                viewModel.crop { processOutput ->

                                    val resultId =
                                        ResultHolder.put(processOutput)

                                    onResult(resultId)
                                }
                            },
                            enabled = state.canCrop,
                            loading = state.isProcessing,
                        )
                    }
                }
            }
        },
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp),
        ) {

            // =========================================================
            // BEFORE IMAGE SELECTION
            // =========================================================
            if (state.imageInfo == null) {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                CropHeaderSection()/*   Image(
                    painter = painterResource(
                        id = R.drawable.crop
                    ),
                    contentDescription = "Crop Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Fit,
                )
*/
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                /*Text(
                    text = "Crop your image",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF214364),
                    textAlign = TextAlign.Center,
                )*/

                Text(
                    text = "Select an image to start cropping",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp,
                    ),
                    color = Color(0xFF263B70),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp,
                        ),
                )
                /*   style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF777784),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 5.dp),
                    )*/

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

            } else {

                // =====================================================
                // SELECTED IMAGE
                // =====================================================
                val info = state.imageInfo!!

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // -----------------------------------------------------
                // Selected Image Header
                // Same placement as PDF:
                // Selected Images              Add More
                // -----------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Selected Image",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF18245C),
                        )

                        Text(
                            text = info.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7A819C),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    // -------------------------------------------------
                    // Choose Different
                    // -------------------------------------------------
                    TextButton(
                        onClick = {
                            pickMedia.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts
                                        .PickVisualMedia
                                        .ImageOnly
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Choose Different",
                            color = Color(0xFF665DF5),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // =====================================================
                // CROP EDITOR
                // =====================================================
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF172033),
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp,
                    ),
                ) {

                    InteractiveCropEditor(
                        uri = info.uri,
                        cropRect = state.cropRect,
                        aspectRatio = state.aspectRatio,
                        onCropRectChange = { rect ->
                            viewModel.updateCropRect(rect)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // =====================================================
                // IMAGE INFORMATION
                // =====================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {

                    CropInfoCard(
                        title = "FILE SIZE",
                        value = FormatUtils.formatBytes(
                            info.sizeBytes
                        ),
                        modifier = Modifier.weight(1f),
                    )

                    CropInfoCard(
                        title = "DIMENSIONS",
                        value = FormatUtils.formatDimensions(
                            info.widthPx,
                            info.heightPx,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }

                // =====================================================
                // ASPECT RATIO
                // =====================================================
                Text(
                    text = "Aspect Ratio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF214364),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                            bottom = 12.dp,
                        ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp),
                ) {

                    CropRatioButton(
                        text = "Free",
                        selected = state.aspectRatio == null,
                        onClick = {
                            viewModel.setAspectRatio(null)
                        },
                    )

                    CropRatioButton(
                        text = "1 : 1",
                        selected = state.aspectRatio == 1f,
                        onClick = {
                            viewModel.setAspectRatio(1f)
                        },
                    )

                    CropRatioButton(
                        text = "4 : 5",
                        selected = state.aspectRatio == 0.8f,
                        onClick = {
                            viewModel.setAspectRatio(0.8f)
                        },
                    )

                    CropRatioButton(
                        text = "16 : 9",
                        selected =
                            state.aspectRatio == 16f / 9f,
                        onClick = {
                            viewModel.setAspectRatio(16f / 9f)
                        },
                    )
                }

                // =====================================================
                // ADJUST
                // =====================================================
                Text(
                    text = "Adjust",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF214364),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                            bottom = 12.dp,
                        ),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp),
                ) {

                    CropToolButton(
                        icon = Icons.Outlined.RotateLeft,
                        text = "Rotate Left",
                        selected =
                            selectedTransform == "left",
                        onClick = {
                            selectedTransform = "left"
                            viewModel.rotateLeft()
                        },
                        modifier = Modifier.weight(1f),
                    )

                    CropToolButton(
                        icon = Icons.Outlined.RotateRight,
                        text = "Rotate Right",
                        selected =
                            selectedTransform == "right",
                        onClick = {
                            selectedTransform = "right"
                            viewModel.rotateRight()
                        },
                        modifier = Modifier.weight(1f),
                    )

                    CropToolButton(
                        icon = Icons.Outlined.Flip,
                        text = "Flip",
                        selected =
                            selectedTransform == "flip",
                        onClick = {
                            selectedTransform = "flip"
                            viewModel.flip()
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                // =====================================================
                // ERROR
                // =====================================================
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            top = 12.dp
                        ),
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }

    // =============================================================
    // PROCESSING
    // =============================================================
    if (state.isProcessing) {
        ProcessingOverlay(
            message = "Cropping image…"
        )
    }
}

@Composable
private fun CropHeaderSection() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp),
        contentAlignment = Alignment.Center,
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.crop
            ),
            contentDescription = "Image compression illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            contentScale = ContentScale.Fit,
        )
    }
}
// =================================================================
// ASPECT RATIO BUTTON
// =================================================================

@Composable
private fun CropRatioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(90.dp)
            .height(50.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) {
            Color(0xFF1EBEB0)
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
        },
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) {
                    Color.White
                } else {
                    Color(0xFF526080)
                },
            )
        }
    }
}


// =================================================================
// IMAGE INFORMATION CARD
// =================================================================

@Composable
private fun CropInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE5E8EF),
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
        ),
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp,
            ),
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF858592),
                letterSpacing = 0.5.sp,
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF214364),
                modifier = Modifier.padding(
                    top = 5.dp
                ),
            )
        }
    }
}


// =================================================================
// ROTATE / FLIP BUTTON
// =================================================================

@Composable
private fun CropToolButton(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(82.dp)
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                Color(0xFF1EBEB0)
            } else {
                Color.White
            }
        ),
        border = if (!selected) {
            BorderStroke(
                1.dp,
                Color(0xFFE1E5EC)
            )
        } else {
            null
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation =
                if (selected) 2.dp else 0.dp
        ),
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center,
        ) {

            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (selected) {
                    Color.White
                } else {
                    Color(0xFF1EBEB0)
                },
                modifier = Modifier.size(25.dp),
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) {
                    Color.White
                } else {
                    Color(0xFF214364)
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    top = 7.dp
                ),
            )
        }
    }
}
