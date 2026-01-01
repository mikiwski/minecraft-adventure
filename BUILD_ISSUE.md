# Problem z buildem

## Status
- ✅ Java 21 zainstalowana i działa
- ✅ Gradle wrapper 8.10 skonfigurowany
- ✅ Wszystkie zależności zaktualizowane
- ❌ Build nie przechodzi z błędem: "Unsupported unpick version"

## Problem
Fabric Loom 1.8.5 nie obsługuje jeszcze w pełni Minecraft 1.21.11. Błąd "Unsupported unpick version" wskazuje na problem z mappings/unpick dla tej wersji.

## Możliwe rozwiązania

### Opcja 1: Użyj najnowszej wersji fabric-loom (jeśli dostępna)
Sprawdź najnowszą wersję na: https://maven.fabricmc.net/net/fabricmc/fabric-loom/

### Opcja 2: Użyj 1.21.1 zamiast 1.21.11 (tymczasowo)
Jeśli 1.21.11 nie jest jeszcze w pełni wspierane, możesz tymczasowo użyć 1.21.1:
- Zmień `minecraft_version=1.21.1` w `gradle.properties`
- Zmień `yarn_mappings=1.21.1+build.XX` (sprawdź dostępne wersje)
- Zmień `fabric_version=0.108.0+1.21.1`

### Opcja 3: Poczekaj na aktualizację Fabric Loom
Fabric Loom może potrzebować aktualizacji do pełnego wsparcia 1.21.11.

## Sprawdź
- Czy fabric-loom 1.8.5+ wspiera 1.21.11: https://github.com/FabricMC/fabric-loom/releases
- Dokumentacja Fabric: https://fabricmc.net/develop/

