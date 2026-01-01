# Problem z "Unsupported unpick version"

## Problem
Build nie przechodzi z błędem: `UnsupportedOperationException: Unsupported unpick version`

## Próbowane rozwiązania:
- ✅ Java 21 zainstalowana
- ✅ Gradle 8.11 skonfigurowany
- ✅ fabric-loom 1.9.2 (najnowsza dostępna)
- ✅ yarn mappings 1.21.11+build.1, build.2, build.3
- ❌ Wszystkie kombinacje nadal dają ten sam błąd

## Możliwe przyczyny:
1. Fabric-loom 1.9.2 może jeszcze nie wspierać w pełni unpick dla 1.21.11
2. Mappings 1.21.11 mogą nie mieć jeszcze pełnych danych unpick
3. Może być potrzebna nowsza wersja fabric-loom (nie jest jeszcze dostępna w Maven)

## Rozwiązanie:
Sprawdź czy jest dostępna nowsza wersja fabric-loom na GitHub:
- https://github.com/FabricMC/fabric-loom/releases
- Może trzeba użyć SNAPSHOT wersji

Lub poczekaj na aktualizację fabric-loom która wspiera 1.21.11.

## Workaround:
Możesz tymczasowo użyć 1.21.1 zamiast 1.21.11 do czasu aż fabric-loom zostanie zaktualizowany.

