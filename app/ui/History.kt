@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var actionItem by remember { mutableStateOf<HistoryItem?>(null) }
    var deleteConfirmItem by remember { mutableStateOf<HistoryItem?>(null) }

    val totalItems = state.filteredItems.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
    ) {
        // ---------------------------------------------------------
        // Header
        // ---------------------------------------------------------
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 24.dp,
                bottom = 8.dp
            )
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF18245C)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your recently processed files",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A819C)
            )
        }

        // ---------------------------------------------------------
        // Summary Card
        // ---------------------------------------------------------
        HistorySummaryCard(
            totalItems = totalItems
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------------------------------------------------
        // Filters
        // ---------------------------------------------------------
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                HistoryFilterChip(
                    text = "All",
                    selected = state.filter == null,
                    onClick = {
                        viewModel.setFilter(null)
                    }
                )
            }

            item {
                HistoryFilterChip(
                    text = "Compress",
                    selected = state.filter == ToolType.COMPRESS,
                    onClick = {
                        viewModel.setFilter(ToolType.COMPRESS)
                    }
                )
            }

            item {
                HistoryFilterChip(
                    text = "Resize",
                    selected = state.filter == ToolType.RESIZE,
                    onClick = {
                        viewModel.setFilter(ToolType.RESIZE)
                    }
                )
            }

            item {
                HistoryFilterChip(
                    text = "Convert",
                    selected = state.filter == ToolType.CONVERT,
                    onClick = {
                        viewModel.setFilter(ToolType.CONVERT)
                    }
                )
            }

            item {
                HistoryFilterChip(
                    text = "Crop",
                    selected = state.filter == ToolType.CROP,
                    onClick = {
                        viewModel.setFilter(ToolType.CROP)
                    }
                )
            }

            item {
                HistoryFilterChip(
                    text = "PDF",
                    selected = state.filter == ToolType.PDF,
                    onClick = {
                        viewModel.setFilter(ToolType.PDF)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ---------------------------------------------------------
        // Content
        // ---------------------------------------------------------
        if (state.filteredItems.isEmpty()) {

            HistoryEmptyState(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = state.filteredItems,
                    key = { it.id }
                ) { item ->

                    HistoryPremiumCard(
                        item = item,
                        onClick = {
                            actionItem = item
                        },
                        onMoreClick = {
                            actionItem = item
                        }
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Action Bottom Sheet
    // -------------------------------------------------------------
    val current = actionItem

    if (current != null) {

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = {
                actionItem = null
            },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(
                topStart = 28.dp,
                topEnd = 28.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 28.dp
                    )
            ) {

                // Handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(42.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9DDEC))
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    HistoryToolIcon(
                        tool = current.tool,
                        size = 52.dp
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = current.outputDisplayName
                                ?: current.inputDisplayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF18245C),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = historyToolLabel(current.tool),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7A819C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                HistoryActionRow(
                    icon = Icons.Filled.FolderOpen,
                    title = "Open output",
                    subtitle = "View the processed file",
                    onClick = {
                        viewModel.openItem(current)
                        actionItem = null
                    }
                )

                HistoryActionRow(
                    icon = Icons.Filled.Share,
                    title = "Share",
                    subtitle = "Share this file",
                    onClick = {
                        viewModel.shareItem(current)
                        actionItem = null
                    }
                )

                HistoryActionRow(
                    icon = Icons.Filled.DeleteOutline,
                    title = "Remove from history",
                    subtitle = "Keep the file on your device",
                    onClick = {
                        viewModel.removeFromHistory(current)
                        actionItem = null
                    }
                )

                HistoryActionRow(
                    icon = Icons.Filled.DeleteForever,
                    title = "Delete file from device",
                    subtitle = "Permanently delete the saved file",
                    destructive = true,
                    onClick = {
                        deleteConfirmItem = current
                        actionItem = null
                    }
                )
            }
        }
    }

    // -------------------------------------------------------------
    // Delete Confirmation
    // -------------------------------------------------------------
    val toDelete = deleteConfirmItem

    if (toDelete != null) {

        AlertDialog(
            onDismissRequest = {
                deleteConfirmItem = null
            },
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    text = "Delete file?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This permanently deletes the saved file from your device. This can't be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFileAndHistory(toDelete)
                        deleteConfirmItem = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deleteConfirmItem = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
private fun HistorySummaryCard(
    totalItems: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFEDEBFF),
                        Color(0xFFE9FBF8)
                    )
                )
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = null,
                tint = Color(0xFF665DF5),
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Processing History",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF18245C)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Your processed files are saved here",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF707895)
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = totalItems.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF665DF5)
            )

            Text(
                text = "Files",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF7A819C)
            )
        }
    }
}
@Composable
private fun HistoryFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) {
            Color(0xFF665DF5)
        } else {
            Color.White
        },
        border = if (!selected) {
            BorderStroke(
                1.dp,
                Color(0xFFE5E7F2)
            )
        } else {
            null
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 9.dp
            ),
            color = if (selected) {
                Color.White
            } else {
                Color(0xFF59617D)
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
@Composable
private fun HistoryPremiumCard(
    item: HistoryItem,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    val sizeSummary =
        if (item.inputSizeBytes != null && item.outputSizeBytes != null) {
            "${FormatUtils.formatBytes(item.inputSizeBytes)} → ${
                FormatUtils.formatBytes(item.outputSizeBytes)
            }"
        } else if (item.pageCount != null) {
            "${item.pageCount} pages"
        } else {
            null
        }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Image + tool badge
            HistoryImageThumbnail(
                uri = item.outputUri?.toUri(),
                tool = item.tool,
               // isPdf = item.tool == ToolType.PDF
            )

            Spacer(modifier = Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.outputDisplayName
                        ?: item.inputDisplayName,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF18245C),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = historyToolLabel(item.tool),
                        style = MaterialTheme.typography.labelMedium,
                        color = toolColor(item.tool)
                    )

                    if (sizeSummary != null) {
                        Text(
                            text = "  •  ",
                            color = Color(0xFFB1B5C7)
                        )

                        Text(
                            text = sizeSummary,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF707895)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = FormatUtils.relativeTimeAgo(item.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9FB2)
                )
            }

            IconButton(
                onClick = onMoreClick
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = Color(0xFF858BA3)
                )
            }
        }
    }
}
@Composable
private fun HistoryImageThumbnail(
    uri: Uri?,
    tool: ToolType,
) {
    var imageError by remember(uri) {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF1F2F8))
    ) {

        if (uri != null && !imageError && tool != ToolType.PDF) {

            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = {
                    imageError = true
                }
            )

        } else {

            // Generic fallback icon
            Icon(
                imageVector = if (tool == ToolType.PDF) {
                    Icons.Filled.Description
                } else {
                    Icons.Filled.Image
                },
                contentDescription = null,
                tint = Color(0xFF9AA0B5),
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)
            )
        }

        // Only tool indicator at bottom-right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .size(25.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(toolColor(tool)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = toolIcon(tool),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
private fun toolIcon(tool: ToolType): ImageVector {
    return when (tool) {
        ToolType.COMPRESS -> Icons.Filled.Compress
        ToolType.RESIZE -> Icons.Filled.PhotoSizeSelectLarge
        ToolType.CONVERT -> Icons.Filled.Transform
        ToolType.CROP -> Icons.Filled.Crop
        ToolType.PDF -> Icons.Filled.PictureAsPdf
        else -> Icons.Filled.Build
    }
}
private fun toolColor(tool: ToolType): Color {
    return when (tool) {
        ToolType.COMPRESS -> Color(0xFF665DF5)
        ToolType.RESIZE -> Color(0xFF229CC8)
        ToolType.CONVERT -> Color(0xFF1EBEB0)
        ToolType.CROP -> Color(0xFFE58B35)
        ToolType.PDF -> Color(0xFFE94B55)
        else -> Color(0xFF6C7187)
    }
}
