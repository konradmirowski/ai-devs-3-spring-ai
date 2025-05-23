package altocumulus.aidevs3.model.s01e03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Optional;

@JsonInclude(Include.NON_EMPTY)
public record TestData(String question, int answer, Optional<Test> test) {}