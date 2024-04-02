import { Controller, Get, Body } from "@nestjs/common";

@Controller("/validate")
export class ValidationController {
  @Get("/message")
  async validateMessage(@Body() payload: any) {}
}
