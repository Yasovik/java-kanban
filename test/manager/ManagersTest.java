package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ManagersTest {

    @Test
    @DisplayName("Проверка что утилитарный класс всегда возвращает проинициализированный getDefault")
    public void initManagerGetDefaultTest() {
        InMemoryTaskManager def = (InMemoryTaskManager) Managers.getDefault();
        Assertions.assertNotNull(def);
    }

    @Test
    @DisplayName("Проверка что утилитарный класс всегда возвращает проинициализированный getDefaultHistory")
    public void initManagerGetDefaultHistoryTest() {
        InMemoryHistoryManager defHistory = (InMemoryHistoryManager) Managers.getDefaultHistory();
        Assertions.assertNotNull(defHistory);
    }
}
