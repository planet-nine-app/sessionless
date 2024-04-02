import {
  Controller,
  Post,
  Body,
  Res
} from "@nestjs/common";
import { CreateUserDto } from "../dtos";
import sessionless from "sessionless-node";
import { UserRepository } from "../repositories";
import { Response } from 'express';

@Controller("/user")
export class UserController {
  @Post("/register")
  async registerUser(@Body() payload: CreateUserDto, @Res() res: Response) {
    try {
      const signature = payload.signature;
      const publicKey = payload.publicKey;

      const message = JSON.stringify({
        publicKey,
        content: payload.content,
        timestamp: payload.timestamp,
      });

      if (sessionless.verifySignature(signature, message, publicKey)) {
        const uuid = sessionless.generateUUID();
        await UserRepository.saveUser(uuid, publicKey);
        const user = {
          uuid,
          welcomeMessage: "Welcome to this example!",
        };
        res.status(201).send(JSON.stringify(user));
      } else {
        throw new Error("Something went wrong. Please try again later.");
      }
    } catch (error) {
      res.status(400).send(`Error: ${error}`);
    }
  }
}
