@Composable
fun HomeScreen(
    onOpenTool: (ToolType) -> Unit,
    onOpenSettings: () -> Unit,
    onViewHistory: () -> Unit
) {
    val backgroundColor = Color(0xFFF7F9FF)
    data class PixelToolItem(
        val title: String,
        val subtitle: String,
        val image: Int,
        val accent: Color,
        val toolType: ToolType? = null,
        val isHistory: Boolean = false
    )

    val toolItems = listOf(
        PixelToolItem(
            title = "Compress",
            subtitle = "Reduce file size",
            image = R.drawable.compress,
            accent = Color(0xFF5B5AF7),
            toolType = ToolType.COMPRESS
        ),
        PixelToolItem(
            title = "Resize",
            subtitle = "Change dimensions",
            image = R.drawable.resize,
            accent = Color(0xFF16BFA7),
            toolType = ToolType.RESIZE
        ),
        PixelToolItem(
            title = "Convert",
            subtitle = "JPG PNG WebP",
            image = R.drawable.convert,
            accent = Color(0xFFFF8A3D),
            toolType = ToolType.CONVERT
        ),
        PixelToolItem(
            title = "Images to PDF",
            subtitle = "Combine images",
            image = R.drawable.pdf,
            accent = Color(0xFFFF5B65),
            toolType = ToolType.PDF
        ),
        PixelToolItem(
            title = "Crop",
            subtitle = "Crop images",
            image = R.drawable.crop,
            accent = Color(0xFF3E8EF7),
            toolType = ToolType.CROP
        ),
        PixelToolItem(
            title = "History",
            subtitle = "Recent files",
            image = R.drawable.history,
            accent = Color(0xFF64748B),
            isHistory = true
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Step 1: App header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                R.drawable.imageease_icon
                            ),
                            contentDescription = "ImageEase",
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "ImageEase",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10245C)
                            )

                            Text(
                                text = "Smart Image Tools",
                                fontSize = 13.sp,
                                color = Color(0xFF53658C)
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                Color.White,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF526B99)
                        )
                    }
                }
            }

            // Step 2: Hero banner
            item {
                Image(
                    painter = painterResource(R.drawable.banner),
                    contentDescription = "Powerful Image Tools",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.FillWidth
                )
            }

                       // Step 3: Tool section heading
                        item {
                            Text(
                                text = "Image Tools",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10245C)
                            )
                        }

                        // Step 4: Two-column tools grid.
                        // This is a regular layout inside LazyColumn,
                        // so there is no nested vertical scrolling.
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                toolItems.chunked(2).forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowItems.forEach { tool ->
                                            PixelToolCard(
                                                title = tool.title,
                                                subtitle = tool.subtitle,
                                                image = tool.image,
                                                accent = tool.accent,
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    if (tool.isHistory) {
                                                        onViewHistory()
                                                    } else {
                                                        tool.toolType?.let { type ->
                                                            onOpenTool(type)
                                                        }
                                                    }
                                                }
                                            )
                                        }

                                        // Keep the final row aligned
                                        // when there is only one card.
                                        if (rowItems.size == 1) {
                                            Spacer(
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                                    // Step 5: Promotional tagline
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .clip(RoundedCornerShape(24.dp))
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xFFE7E8FF),
                                                            Color(0xFFE5FAF7),
                                                            Color(0xFFFFF0E5)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "Small tools.",
                                                    fontFamily = FontFamily.Cursive,
                                                    fontSize = 17.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    fontStyle = FontStyle.Italic,
                                                    color = Color(0xFF52608A)
                                                )

                                                Text(
                                                    text = "Big difference.",
                                                    fontSize = 22.sp,
                                                    fontFamily = FontFamily.Cursive,
                                                    fontWeight = FontWeight.Bold,
                                                    fontStyle = FontStyle.Italic,
                                                    color = Color(0xFF10245C)
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = "Edit • Convert • Simplify",
                                                    fontFamily = FontFamily.Cursive,
                                                    fontSize = 12.sp,
                                                    fontStyle = FontStyle.Italic,
                                                    color = Color(0xFF6B7280)
                                                )
                                            }
                                    }    }

        }
    }
}
@Composable
fun PixelToolCard(
    title: String,
    subtitle: String,
    image: Int,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    // Step 23: Create a rounded premium tool card
    Card(
        modifier = modifier
            .height(153.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        // Step 24: Arrange the tool content vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            // Step 25: Create the tool illustration area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp),
                contentAlignment = Alignment.Center
            ) {

                // Step 26: Display the tool illustration
                Image(
                    painter = painterResource(image),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Step 27: Create the title, subtitle and arrow row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Step 28: Display the tool title and description
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10245C),
                        maxLines = 1
                    )

                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color(0xFF68789C),
                        maxLines = 1
                    )
                }

                // Step 29: Add the colored navigation arrow
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open $title",
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
