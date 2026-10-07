@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // Step 1: Control when the splash animation starts
    var animateIn by remember { mutableStateOf(false) }

    // Step 2: Animate the logo from small to normal size
    val logoScale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splashLogoScale",
    )

    // Step 3: Fade the app logo into the screen
    val logoAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(500),
        label = "splashLogoAlpha",
    )

    // Step 4: Fade in the tagline slightly after the logo
    val taglineAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 250
        ),
        label = "splashTaglineAlpha",
    )

    // Step 5: Start the animation and move to the next screen
    LaunchedEffect(Unit) {
        animateIn = true

        // Keep the splash screen visible for 5 seconds
        delay(5000)

        // Open the next screen
        onFinished()
    }

    // Step 6: Create the full-screen splash background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color.White
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {

        // Step 7: Add the full-screen background illustration
        Image(
            painter = painterResource(id = R.drawable.image_bg),
            contentDescription = "App Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Step 8: Place the logo and text in the center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Step 9: Show the animated ImageEase icon
            Image(
                painter = painterResource(id = R.drawable.imageease_icon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentScale = ContentScale.Fit
            )

            // Step 10: Show the app name
            Text(
                text = "ImageEase",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black.copy(alpha = logoAlpha),
                modifier = Modifier.padding(top = 24.dp),
            )

            // Step 11: Show the animated tagline
            Text(
                text = "Resize. Compress. Convert. PDF.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(
                    alpha = taglineAlpha * 0.75f
                ),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
