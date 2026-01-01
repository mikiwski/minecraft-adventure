# Instrukcja testowania moda

## Wymagania
- Java 21 lub nowsza
- Gradle (lub użyj gradlew wrapper)
- Minecraft 1.21.11
- Fabric Loader 0.16.9+

## Budowanie projektu

### Opcja 1: Użyj Gradle Wrapper (zalecane)
```bash
# Utwórz gradle wrapper (jeśli nie istnieje)
gradle wrapper --gradle-version 8.5

# Zbuduj projekt
./gradlew build

# Plik .jar będzie w: build/libs/adventure-1.0.0.jar
```

### Opcja 2: Użyj lokalnego Gradle
```bash
gradle build
```

## Uruchomienie w środowisku deweloperskim

### Uruchomienie klienta (z modem)
```bash
./gradlew runClient
```

### Uruchomienie serwera
```bash
./gradlew runServer
```

## Instalacja w grze

1. Zbuduj projekt: `./gradlew build`
2. Skopiuj plik `build/libs/adventure-1.0.0.jar` do folderu `mods` w katalogu Minecraft
3. Uruchom Minecraft 1.21.11 z Fabric Loader

## Testowanie funkcji

1. **HUD Overlay**: Po uruchomieniu gry powinieneś zobaczyć listę zadań po prawej stronie ekranu
2. **GUI Screen**: Naciśnij klawisz `T` aby otworzyć ekran z listą zadań
3. **Zadania**: Spróbuj zebrać przedmioty, craftować lub zabijać moby - zadania powinny się aktualizować automatycznie
4. **Nagrody**: Po ukończeniu zadania powinieneś otrzymać XP i przedmioty

## Rozwiązywanie problemów

### Błąd: "Gradle wrapper not found"
```bash
gradle wrapper --gradle-version 8.5
```

### Błąd: "Minecraft version mismatch"
Sprawdź czy w `gradle.properties` jest ustawione `minecraft_version=1.21.11`

### Błąd: "Fabric API not found"
Upewnij się że `fabric_version` w `gradle.properties` jest zgodna z wersją dla MC 1.21.11

