package io.github.yilengyao.openaiapplication.graphql;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import io.github.yilengyao.openai.client.OpenAiClient;
import io.github.yilengyao.openai.graphql.generated.types.ChatCompletionInput;
import io.github.yilengyao.openai.graphql.generated.types.ChatCompletionResult;
import io.github.yilengyao.openai.graphql.generated.types.CompletionInput;
import io.github.yilengyao.openai.graphql.generated.types.CompletionOutput;
import io.github.yilengyao.openai.graphql.generated.types.CreateImageInput;
import io.github.yilengyao.openai.graphql.generated.types.EditImageInput;
import io.github.yilengyao.openai.graphql.generated.types.EditInput;
import io.github.yilengyao.openai.graphql.generated.types.EditResponse;
import io.github.yilengyao.openai.graphql.generated.types.ImageResponse;
import io.github.yilengyao.openai.graphql.generated.types.ModelsOutput;
import io.github.yilengyao.openai.graphql.generated.types.TranscriptionInput;
import io.github.yilengyao.openai.graphql.generated.types.TranslationInput;
import io.github.yilengyao.openai.graphql.generated.types.TextResponse;
import io.github.yilengyao.openai.model.audio.TranscriptionPayload;
import io.github.yilengyao.openai.model.audio.TranslationPayload;
import io.github.yilengyao.openai.model.chat.ChatCompletionChunk;
import io.github.yilengyao.openai.model.chat.ChatCompletionPayload;
import io.github.yilengyao.openai.model.completion.CompletionPayload;
import io.github.yilengyao.openai.model.edit.EditPayload;
import io.github.yilengyao.openai.model.image.CreateImagePayload;
import io.github.yilengyao.openai.model.image.EditImagePayload;

@DgsComponent
public class OpenAiDataFetcher {

  private final OpenAiClient openAiClient;

  @Autowired
  public OpenAiDataFetcher(OpenAiClient openAiClient) {
    this.openAiClient = openAiClient;
  }

  @DgsMutation
  public TextResponse createTranscription(
      @InputArgument("file") MultipartFile file,
      @InputArgument("transcriptionInput") TranscriptionInput transcriptionInput) throws IOException {
    return openAiClient.createTranscription(TranscriptionPayload.fromGraphQl(file, transcriptionInput))
        .toGraphQl();
  }

  @DgsMutation
  public TextResponse createTranslation(
      @InputArgument("file") MultipartFile file,
      @InputArgument("translationInput") TranslationInput translationInput) throws IOException {
    return openAiClient.createTranslation(TranslationPayload.fromGraphQl(file, translationInput))
        .toGraphQl();
  }

  @DgsQuery
  public ModelsOutput models(
      @InputArgument("id") Optional<String> id) {
    return id.isPresent()
        ? openAiClient.models(id.get()).toGraphQl()
        : openAiClient.models().toGraphQl();
  }

  @DgsMutation
  public CompletionOutput completion(
      @InputArgument("completionInput") CompletionInput completionInput) {
    return openAiClient
        .completion(CompletionPayload.fromGraphQl(completionInput))
        .toGraphQl();
  }

  @DgsMutation
  public ChatCompletionResult chatCompletion(
      @InputArgument("chatInput") ChatCompletionInput chatInput) {
    if (chatInput.getStream() != null && chatInput.getStream()) {
      return openAiClient
          .streamChatCompletion(ChatCompletionPayload.fromGraphQl(chatInput))
          .next()
          .map(ChatCompletionChunk::toGraphQl)
          .block();
    } else {
      return openAiClient
          .createChatCompletion(ChatCompletionPayload.fromGraphQl(chatInput))
          .toGraphQl();

    }
  }

  @Deprecated
  @DgsMutation
  public EditResponse edit(
      @InputArgument("editInput") EditInput editInput) {
    return openAiClient
        .edit(EditPayload.fromGraphQl(editInput))
        .toGraphQl();
  }

  @DgsMutation
  public ImageResponse createImage(
      @InputArgument("createImageInput") CreateImageInput createImageInput) {
    return openAiClient
        .createImage(CreateImagePayload.fromGraphQl(createImageInput))
        .toGraphQl();
  }

  @DgsMutation
  public ImageResponse editImage(
      @InputArgument("image") MultipartFile image,
      @InputArgument("mask") MultipartFile mask,
      @InputArgument("editImageInput") EditImageInput editImageInput) throws IOException {
    return openAiClient.editImage(EditImagePayload.fromGraphQl(image, mask, editImageInput)).toGraphQl();
  }

}
