# Instalacja Java dla projektu

## Problem
Projekt wymaga Java 17 lub 21, a obecnie masz zainstalowaną tylko Java 8.

## Rozwiązanie

### Opcja 1: Zainstaluj Java 21 (zalecane)
```bash
# Użyj Homebrew (jeśli masz)
brew install openjdk@21

# Lub pobierz z Oracle/Adoptium:
# https://adoptium.net/temurin/releases/?version=21
```

### Opcja 2: Zainstaluj Java 17
```bash
brew install openjdk@17
```

### Po instalacji:

1. Ustaw JAVA_HOME:
```bash
# Dla Java 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Dla Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

2. Dodaj do ~/.zshrc (dla stałego ustawienia):
```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc
```

3. Sprawdź wersję:
```bash
java -version
# Powinno pokazać: openjdk version "21.x.x" lub "17.x.x"
```

4. Zbuduj projekt:
```bash
cd /Users/mikiwski/Projekty/MinecraftAdventure
./gradlew build
```

## Alternatywnie: użyj IntelliJ IDEA lub innego IDE
IDE często mają wbudowane zarządzanie wersjami Java i mogą automatycznie pobrać odpowiednią wersję.

