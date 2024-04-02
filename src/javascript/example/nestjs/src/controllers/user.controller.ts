import { Controller, Get, Post, Body, HttpException, HttpStatus } from "@nestjs/common";
import { CreateUserDto } from "../dtos";
import sessionless from "sessionless-node";
import { UserRepository } from "../repositories";

@Controller("/user")
export class UserController {
  @Post("/register")
  async registerUser(@Body() payload: CreateUserDto) {
    try {
      const signature = payload.signature;
    const publicKey = payload.publicKey;

    const message = JSON.stringify({
      publicKey,
      content: payload.content,
      timestamp: payload.timestamp,
    });

    if (sessionless.verifySignature(signature, message, publicKey)) {
      const uuid = sessionless.generateUUID()
      await UserRepository.saveUser(uuid, publicKey)
      const user = {
        uuid,
        welcomeMessage: 'Welcome to this example!'
      }
      return JSON.stringify(user)
    }
    else {
      throw new Error('Something went wrong. Please try again later.')
    }
    }
    catch(error) {
      throw new HttpException(`Error: ${error}`, HttpStatus.BAD_REQUEST)
    }
  }
}
