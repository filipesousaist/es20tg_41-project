package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StatementQuestionDto implements Serializable {
    private String content;
    private List<StatementOptionDto> options;
    private ImageDto image;
    private Integer sequence;
    private Integer questionId;
    private ClarificationRequestDto clarificationRequest;
    public StatementQuestionDto(QuestionAnswer questionAnswer) {
        this.content = questionAnswer.getQuizQuestion().getQuestion().getContent();
        if (questionAnswer.getQuizQuestion().getQuestion().getImage() != null) {
            this.image = new ImageDto(questionAnswer.getQuizQuestion().getQuestion().getImage());
        }
        this.options = questionAnswer.getQuizQuestion().getQuestion().getOptions().stream().map(StatementOptionDto::new).collect(Collectors.toList());
        this.sequence = questionAnswer.getSequence();
        this.questionId = questionAnswer.getQuizQuestion().getQuestion().getId();

        Optional<ClarificationRequest> cr =
                questionAnswer.getQuizQuestion().getQuestion().getClarificationRequests().stream()
                        .filter(c -> questionAnswer.getQuizAnswer().getUser().getId() == c.getStudent().getId())
                        .findAny();

        cr.ifPresent(request -> this.clarificationRequest = new ClarificationRequestDto(request));

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<StatementOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<StatementOptionDto> options) {
        this.options = options;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public ClarificationRequestDto getClarificationRequest() {
        return clarificationRequest;
    }

    public void setClarificationRequest(ClarificationRequestDto clarificationRequest) {
        this.clarificationRequest = clarificationRequest;
    }
}