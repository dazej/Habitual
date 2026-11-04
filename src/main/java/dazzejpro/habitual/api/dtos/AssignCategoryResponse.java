package dazzejpro.habitual.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name ="AssignCategoryResponse", description = "A habit that has been assigned a category")
public class AssignCategoryResponse {
    @Schema(description = "Id of the habit", example = "1")
    private Integer habitId;
    @Schema(description = "Id of the category", example = "1")
    private Integer categoryId;
    @Schema(description = "Name of the category", example = "Health")
    private String categoryName;

    public AssignCategoryResponse(
            Integer habitId,
            Integer categoryId,
            String categoryName) {
        this.habitId = habitId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Integer getHabitId() {
        return habitId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}