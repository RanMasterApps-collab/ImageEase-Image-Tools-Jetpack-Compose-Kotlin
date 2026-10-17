@Composable
fun CompressScreen(
    onBack: () -> Unit,
    onResult: (String) -> Unit,
    onOpenConvert: (() -> Unit)? = null,
    viewModel: CompressViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),

        topBar = {
            ToolTopBar(
                title = "Image Compression",
                onBack = onBack,
            )
        },

        bottomBar = {
            Surface(
                color = Color(0xFFF8F9FF),
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
                                .height(56.dp)
                            ,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1EBEB0),
                                contentColor = Color.White,
                            ),
                        ) {
                            Text("Select Image")
                        }

                    } else {

                        PrimaryActionButton(
                            text = "Compress Image",
                            onClick = {
                                viewModel.compress(onResult)
                            },
                            enabled = state.canCompress,
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
        ) {

            // ---------------------------------------------------------
            // HEADER / ILLUSTRATION
            // ---------------------------------------------------------
            if (state.imageInfo == null) {

                CompressHeaderSection()

                // ---------------------------------------------------------
                // DESCRIPTION
                // ---------------------------------------------------------

                Text(
                    text = "Reduce image file size significantly while maintaining visual quality.",
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
            }
            Spacer(modifier = Modifier.height(12.dp))

            val info = state.imageInfo

            // ---------------------------------------------------------
            // NO IMAGE SELECTED
            // ---------------------------------------------------------

            if (info == null) {


                if (state.errorMessage != null) {
                    ErrorMessage(
                        message = state.errorMessage!!
                    )
                }

            } else {

                // -----------------------------------------------------
                // IMAGE PREVIEW
                // -----------------------------------------------------

                ImagePreviewCard(
                    uri = info.uri,
                    contentDescription = "Selected image",
                )

                Spacer(modifier = Modifier.height(12.dp))

                // -----------------------------------------------------
                // FILE INFORMATION
                // -----------------------------------------------------

                FileInfoCard(
                    size = FormatUtils.formatBytes(info.sizeBytes),
                    dimensions = FormatUtils.formatDimensions(
                        info.widthPx,
                        info.heightPx,
                    ),
                    format = info.format?.label ?: "Unknown",
                )

                // -----------------------------------------------------
                // CHANGE IMAGE
                // -----------------------------------------------------

                TextButton(
                    onClick = {
                        pickMedia.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = "Choose a different image",
                        color = Color(0xFF5B4BFF),
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                // -----------------------------------------------------
                // JPEG / WEBP QUALITY CONTROLS
                // -----------------------------------------------------

                if (state.supportsQuality) {

                    Spacer(modifier = Modifier.height(4.dp))

                    QualitySection(
                        quality = state.quality,
                        onQualityChange = viewModel::onQualityChange,
                    )

                    // -------------------------------------------------
                    // REDUCE DIMENSIONS
                    // -------------------------------------------------

                    Spacer(modifier = Modifier.height(16.dp))

                    ReduceDimensionsCard(
                        enabled = state.reduceDimensions,
                        onToggle = viewModel::onReduceDimensionsToggle,
                    )

                    if (state.reduceDimensions) {

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {

                            ReductionChip(
                                text = "75%",
                                selected = state.reductionOption ==
                                        ReductionOption.P75,
                                onClick = {
                                    viewModel.onReductionOptionChange(
                                        ReductionOption.P75
                                    )
                                },
                                modifier = Modifier.weight(1f),
                            )

                            ReductionChip(
                                text = "50%",
                                selected = state.reductionOption ==
                                        ReductionOption.P50,
                                onClick = {
                                    viewModel.onReductionOptionChange(
                                        ReductionOption.P50
                                    )
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    // -------------------------------------------------
                    // ESTIMATED RESULT
                    // -------------------------------------------------

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Estimated Result",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15265B),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val estimated = state.estimatedBytes

                    BeforeAfterCompressionCard(
                        beforeValue = FormatUtils.formatBytes(
                            info.sizeBytes
                        ),
                        afterValue = if (estimated != null) {
                            "~${FormatUtils.formatBytes(estimated)}"
                        } else {
                            "…"
                        },
                        savedValue = state.percentSaved?.let {
                            "~${
                                FormatUtils.formatBytes(
                                    (
                                            info.sizeBytes -
                                                    (estimated ?: info.sizeBytes)
                                            ).coerceAtLeast(0)
                                )
                            } ($it%)"
                        },
                    )

                    Text(
                        text = "This is an estimate. The final size is shown after processing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF78829C),
                        modifier = Modifier.padding(top = 8.dp),
                    )

                } else {

                    // -------------------------------------------------
                    // PNG LOSSLESS CARD
                    // -------------------------------------------------

                    PngLosslessCard(
                        onOpenConvert = onOpenConvert,
                        imageUri = info.uri,
                    )
                }

                // -----------------------------------------------------
                // ERROR
                // -----------------------------------------------------

                if (state.errorMessage != null) {
                    ErrorMessage(
                        message = state.errorMessage!!
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }

    // -------------------------------------------------------------
    // PROCESSING OVERLAY
    // -------------------------------------------------------------

    if (state.isProcessing) {
        ProcessingOverlay(
            message = "Compressing image…"
        )
    }
}
@Composable
private fun CompressHeaderSection() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp),
        contentAlignment = Alignment.Center,
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.compress
            ),
            contentDescription = "Image compression illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            contentScale = ContentScale.Fit,
        )
    }
}
@Composable
private fun CompressionImageShape(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                color = Color(0xFFB9DDF4),
                shape = RoundedCornerShape(18.dp),
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {

        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = Color(0xFF38A9D8),
            modifier = Modifier.size(48.dp),
        )
    }
}
@Composable
private fun QualitySection(
    quality: Int,
    onQualityChange: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = "Quality",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF17275E),
                )

                Text(
                    text = "$quality%",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF283A78),
                )
            }

            Slider(
                value = quality.toFloat(),
                onValueChange = {
                    onQualityChange(it.roundToInt())
                },
                valueRange = 10f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF6955F6),
                    activeTrackColor = Color(0xFF6955F6),
                    inactiveTrackColor = Color(0xFFE8E9F5),
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0F1FF))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6955F6)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp),
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Smaller size, same great quality!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4C5780),
                )
            }
        }
    }
}
@Composable
private fun ReduceDimensionsCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Column(
                modifier = Modifier.weight(1f),
            ) {

                Text(
                    text = "Also reduce dimensions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF18285F),
                )

                Text(
                    text = "Shrinks pixel size for extra savings",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A8299),
                )
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF6555F5),
                ),
            )
        }
    }
}
@Composable
private fun ReductionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF6555F5),
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = Color(0xFF536080),
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color(0xFFE1E4F0),
            selectedBorderColor = Color.Transparent,
        ),
    )
}
@Composable
private fun BeforeAfterCompressionCard(
    beforeValue: String,
    afterValue: String,
    savedValue: String?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            CompressionSizeItem(
                title = "Original",
                value = beforeValue,
                modifier = Modifier.weight(1f),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F1FF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF6555F5),
                )
            }

            CompressionSizeItem(
                title = "Compressed",
                value = afterValue,
                valueColor = Color(0xFF13B993),
                modifier = Modifier.weight(1f),
            )
        }

        if (savedValue != null) {

            HorizontalDivider(
                color = Color(0xFFECEEF5)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {

                Text(
                    text = "Estimated savings",
                    color = Color(0xFF707991),
                )

                Text(
                    text = savedValue,
                    color = Color(0xFF13B993),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun CompressionSizeItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color(0xFF19285C),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF18285F),
        )

        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
@Composable
private fun PngLosslessCard(
    onOpenConvert: (() -> Unit)?,
    imageUri: Uri,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAF7F4)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF13B993),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "PNG is lossless",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF18285F),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "A quality slider wouldn't shrink a PNG since it never loses detail. PixelKit re-saves it with lossless optimization. For a smaller file, convert it to WebP or JPG instead.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF606A83),
                lineHeight = 19.sp,
            )

            if (onOpenConvert != null) {

                TextButton(
                    onClick = {
                        PendingImageHolder.offer(imageUri)
                        onOpenConvert()
                    },
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(
                        text = "Convert to WebP/JPG for smaller file",
                        color = Color(0xFF5E50EF),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
@Composable
private fun FileInfoCard(
    size: String,
    dimensions: String,
    format: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            FileInfoItem(
                title = "Size",
                value = size,
                modifier = Modifier.weight(1f),
            )

            VerticalDivider(
                modifier = Modifier.height(38.dp),
                color = Color(0xFFE6E8F0),
            )

            FileInfoItem(
                title = "Dimensions",
                value = dimensions,
                modifier = Modifier.weight(1f),
            )

            VerticalDivider(
                modifier = Modifier.height(38.dp),
                color = Color(0xFFE6E8F0),
            )

            FileInfoItem(
                title = "Format",
                value = format,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FileInfoItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF7A829A),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF17275C),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
@Composable
private fun ErrorMessage(
    message: String,
) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(
            start = 8.dp,
            top = 12.dp,
            end = 8.dp,
        ),
    )
}
