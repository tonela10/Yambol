### Yambol 🏀

A comprehensive Android basketball coaching app that empowers coaches to enhance team performance through intelligent player development tracking.

## Overview

Yambol is built with modern Android development practices using Kotlin and Jetpack Compose. The app focuses on granular player development rather than just administrative tasks, providing coaches with detailed insights into player progress through data-driven analytics.

## Features

### 🏆 Team Management
- Create and manage multiple teams
- Comprehensive team roster organization
- Team-wide performance analytics

### 👤 Player Profiling
- Individual player profiles with detailed information
- Ability ratings system (1-5 scale) across basketball fundamentals
- Visual radar charts for skill assessment
- Player development tracking over time

### 📈 Training Session Management
- Plan and record training sessions
- Session-specific player performance tracking
- Training history and progress analytics
- Customizable training objectives

### 🎯 Goal Tracking
- Objective-based goal setting for individual players
- Progress monitoring and milestone tracking
- Performance insights and recommendations
- Data-driven coaching decision support

### 📊 Analytics & Insights
- Player performance radar charts
- Training history visualization
- Progress analytics and trends
- Performance comparison tools

## Technical Stack

### Architecture
- **Clean Architecture** principles for maintainable and testable code
- **MVVM** pattern with ViewModels and LiveData/StateFlow
- **Repository** pattern for data abstraction

### Technologies
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern declarative UI toolkit
- **Room Database** - Local data persistence
- **Material Design 3** - Modern UI design system
- **Coroutines** - Asynchronous programming
- **Hilt/Dagger** - Dependency injection
- **Kotest** - Code testing --> To implement

### Key Features
- **Offline-first** functionality with local Room database storage
- **Responsive UI** that adapts to different screen sizes
- **Data visualization** with interactive charts and graphs

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Minimum SDK: API 21 (Android 5.0)
- Target SDK: API 34 (Android 14)
- Kotlin 1.9.0 or later

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/yambol.git
cd yambol
```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Run the app on an emulator or physical device

### Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## Database Schema

Yambol uses Room database with the following main entities:
- **Team** - Team information and metadata
- **Player** - Player profiles and basic information
- **Ability** - Player skill ratings and assessments
- **TrainingSession** - Training session data and attendance
- **Goal** - Player objectives and development targets

## Privacy & Data

- All data is stored locally on the device using Room database
- No personal information is transmitted to external servers
- Offline-first approach ensures data privacy and availability

## Roadmap

- [ ] Cloud sync and backup functionality
- [ ] Advanced analytics and machine learning insights
- [ ] Multi-language support
- [ ] Export functionality (PDF reports, CSV data)
- [ ] Video analysis integration
- [ ] Team communication features

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Create an issue on GitHub
- Contact the development team
- Check the documentation wiki

## Acknowledgments

- Basketball coaching community for feature inspiration
- Android development community for technical guidance
- Open source libraries and tools that make this project possible

---

**Yambol** - Empowering coaches through intelligent player development tracking 🏀
