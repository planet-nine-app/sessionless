import { Controller, Get, Body, Res } from "@nestjs/common";
import sessionless from "sessionless-node";
import { ValidateMessageDto } from "../dtos";
import { UserRepository } from "../repositories";
import { Response } from "express";
@Controller("/validate")
export class ValidationController {
  @Get("/message")
  async validateMessage(
    @Body() payload: ValidateMessageDto,
    @Res() res: Response
  ) {
    try {
      const signature = payload.signature;
      const uuid = payload.uuid;

      const publicKey = await UserRepository.findPublicKeyFromUuid(uuid);

      const message = JSON.stringify({
        content: payload.content,
        timestamp: payload.timestamp,
      });

      if (sessionless.verifySignature(signature, message, publicKey)) {
        const response = JSON.stringify(
          "The message content was verified successfully"
        );
        res.status(202).send(response);
      } else {
        throw new Error("Invalid request parameters provided");
      }
    } catch (error) {
      res.status(400).send(`Error: ${error}`);
    }
  }
}
