
private fun descriptionFor(format: ImageFormat): String = when (format) {
    ImageFormat.JPG -> "Best for photos and broad compatibility."
    ImageFormat.PNG -> "Best when transparency is needed."
    ImageFormat.WEBP -> "Often smaller for web/app use."
}

@Composable
fun ConvertScreen(
    onBack: () -> Unit,
    onResult: (String) -> Unit,
    viewModel: ConvertViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) viewModel.onImageSelected(uri) }

    LaunchedEffect(Unit) {
        PendingImageHolder.consume()?.let { viewModel.onImageSelected(it) }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        topBar = { ToolTopBar(title = "Convert Image", onBack = onBack) },
        bottomBar = {
            Surface(
               color = Color(0xFFF8F9FF)// color = MaterialTheme.colorScheme.background,
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
                    if (state.imageInfo == null) {

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

                    } else {

                        PrimaryActionButton(
                            text = "Convert Image",
                            onClick = {
                                viewModel.convert(onResult)
                            },
                            enabled = state.canConvert,
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(20.dp))
        if(state.imageInfo==null){

         ConvertHeaderSection()

        Text(
            text = "Convert between JPG, PNG, and WebP.",
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
        )}

        // Empty space / selected image content starts here

            val info = state.imageInfo
            if (info == null) {
             } else {
                ImagePreviewCard(uri = info.uri, contentDescription = "Selected image")
                FileInfoRow(
                    items = listOf(
                        FileInfoItem("Input", info.format?.label ?: "Unknown"),
                        FileInfoItem("File", info.displayName),
                        FileInfoItem("Size", FormatUtils.formatBytes(info.sizeBytes)),
                    ),
                    modifier = Modifier.padding(top = 16.dp),
                )
                TextButton(
                    onClick = {
                        pickMedia.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(
                        text = "Choose a different image",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF665DF5),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Output Format",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF214364),
                        modifier = Modifier.padding(
                            top = 22.dp,
                            bottom = 12.dp
                        ),
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Choose one",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF8A90A3),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ImageFormat.entries
                        .filter { it != info.format }
                        .forEach { format ->
                            FormatCard(
                                title = format.label,
                                description = descriptionFor(format),
                                selected = state.targetFormat == format,
                                onClick = { viewModel.onTargetFormatSelected(format) },
                            )
                        }
                }

                if (state.showTransparencyWarning) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF7ED)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = "Transparency detected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9A5B13),
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "JPG does not support transparency. "
                                        + "Choose a background color for transparent areas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7A6652),
                                lineHeight = 18.sp,
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FilterChip(
                                    selected = state.backgroundOption == BackgroundOption.WHITE,
                                    onClick = {
                                        viewModel.onBackgroundOptionChange(
                                            BackgroundOption.WHITE
                                        )
                                    },
                                    label = {
                                        Text(
                                            "White",
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                )

                                FilterChip(
                                    selected = state.backgroundOption == BackgroundOption.BLACK,
                                    onClick = {
                                        viewModel.onBackgroundOptionChange(
                                            BackgroundOption.BLACK
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Black",
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                )
                            }
                        }
                    }
                }

                if (state.showQualitySlider) {
                    QualitySlider(
                        LocalContext.current,
                        quality = state.quality,
                        onQualityChange = viewModel::onQualityChange,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }

                if (state.errorMessage != null) {
                    Text(text = state.errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
                }
            }
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
        }
    }

    if (state.isProcessing) {
        ProcessingOverlay(message = "Converting image…")
    }
}
@Composable
private fun ConvertHeaderSection() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp),
        contentAlignment = Alignment.Center,
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.convert
            ),
            contentDescription = "Image compression illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            contentScale = ContentScale.Fit,
        )
    }
}
