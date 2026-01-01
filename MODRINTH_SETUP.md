# Modrinth Publishing Setup

## Krok 1: Utwórz projekt na Modrinth

1. Zaloguj się na https://modrinth.com
2. Kliknij "Create project"
3. Wypełnij formularz i zapisz **Project ID** (np. `mikiwski-task-adventure`)

## Krok 2: Utwórz token API

1. Przejdź do https://modrinth.com/settings/tokens
2. Kliknij "Create token"
3. Nadaj nazwę (np. "Gradle Upload")
4. Skopiuj token (pokazuje się tylko raz!)

## Krok 3: Skonfiguruj gradle.properties

Otwórz `gradle.properties` i dodaj:

```properties
modrinth_token=TWÓJ_TOKEN_TUTAJ
modrinth_project_id=TWÓJ_PROJECT_ID_TUTAJ
```

**UWAGA:** `gradle.properties` jest w `.gitignore` - token nie będzie commitowany!

## Krok 4: Publikuj

```bash
# Najpierw zbuduj
./gradlew build

# Potem opublikuj na Modrinth (używa task publishModrinth który sprawdza konfigurację)
./gradlew publishModrinth

# Lub bezpośrednio (bez walidacji)
./gradlew modrinth
```

## Alternatywnie: użyj zmiennej środowiskowej

Zamiast w `gradle.properties`, możesz ustawić token jako zmienną środowiskową:

```bash
export MODRINTH_TOKEN=twój_token
export MODRINTH_PROJECT_ID=twój_project_id
./gradlew publishModrinth
```

## Co się dzieje?

- Task `publishModrinth` automatycznie:
  - Pobiera aktualną wersję z `gradle.properties`
  - Uploaduje JAR na Modrinth
  - Ustawia zależności (Fabric API, Fabric Loader)
  - Dodaje changelog

## Troubleshooting

- **"Modrinth token not found"** - upewnij się że token jest w `gradle.properties` lub zmiennej środowiskowej
- **"Please set modrinth_project_id"** - upewnij się że project ID jest ustawiony
- **"Project not found"** - sprawdź czy project ID jest poprawny

