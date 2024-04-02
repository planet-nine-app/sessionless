import Koa from 'koa'
import json from 'koa-json'
import KoaRouter from 'koa-router'
import bodyParser from 'koa-bodyparser'
import sessionless from 'sessionless-node'
import fs from 'fs/promises'

const app = new Koa()
const router = new KoaRouter()

// JSON Prettier Middleware
app.use(json())

// Bodypaser Middleware
app.use(bodyParser({ enableTypes: ['json', 'text', 'form', 'xml'] }))

// Router Middleware
app.use(router.routes()).use(router.allowedMethods())

// Helper methods
const getUsers = async () => {
  const usersString = await fs.readFile('./users.json')
  const users = JSON.parse(usersString)
  return users
}

const saveUser = async (uuid, publicKey) => {
  const usersJson = await getUsers()
  const users = usersJson.users
  users.push({ uuid, publicKey })
  await fs.writeFile('./users.json', JSON.stringify({ users }), 'utf8')
}

const findUserFromPublicKey = async (publicKey) => {
  try {
    const usersString = fs.readFileSync('./users.json')
    const users = JSON.parse(usersString)

    const targetUser = users.find((user) => user.publicKey === publicKey)
    if (targetUser) {
      return targetUser
    } else {
      throw new Error('User not found')
    }
  } catch (error) {
    console.error('Error: ', error)
  }
}

const findPublicKeyFromUuid = async (uuid) => {
  try {
    const usersString = await fs.readFile('./users.json', 'utf8')
    const users = JSON.parse(usersString)

    const targetUser = users.find((user) => user.uuid === uuid)
    if (targetUser) {
      return targetUser
    } else {
      throw new Error('User not found')
    }
  } catch (error) {
    console.error('Error: ', error)
  }
}

// Server routes
router.post('/register', registerUser)
router.get('/message/verify', verifyMessage)

// Register user
async function registerUser (ctx) {
  try {
    const payload = ctx.request.body
    const signature = payload.signature
    const publicKey = payload.publicKey

    const message = JSON.stringify({
      publicKey,
      content: payload.content,
      timestamp: payload.timestamp
    })

    if (sessionless.verifySignature(signature, message, publicKey)) {
      const uuid = sessionless.generateUUID()
      await saveUser(uuid, publicKey)
      const user = {
        uuid,
        welcomeMessage: 'Welcome to this example!'
      }
      ctx.status = 201
      ctx.body = JSON.stringify(user)
    } else {
      throw new Error('Something went wrong. Please try again later.')
    }
  } catch (error) {
    ctx.status = 400
    ctx.body = JSON.stringify({ Error: error.message })
  }
}

// Validate message on behalf of a signer
async function verifyMessage (ctx) {
  try {
    const payload = ctx.request.body
    const signature = payload.signature
    const uuid = payload.uuid

    const publicKey = await findPublicKeyFromUuid(uuid)

    const message = JSON.stringify({
      content: payload.content,
      timestamp: payload.timestamp
    })

    if (sessionless.verifySignature(signature, message, publicKey)) {
      ctx.status = 202
      ctx.body = JSON.stringify(
        'The message content was verified successfully'
      )
    } else {
      throw new Error('Invalid request parameters provided')
    }
  } catch (error) {
    ctx.status = 400
    ctx.body = JSON.stringify({ Error: error.message })
  }
}

// Start server
app.listen(3000)
